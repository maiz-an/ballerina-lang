/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.ballerinalang.compiler.bir.codegen.interop;

import io.ballerina.types.Env;
import org.ballerinalang.compiler.BLangCompilerException;
import org.ballerinalang.model.elements.PackageID;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmCastGen;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmCodeGenUtil;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmErrorGen;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmInstructionGen;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmPackageGen;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmTerminatorGen;
import org.wso2.ballerinalang.compiler.bir.codegen.JvmTypeGen;
import org.wso2.ballerinalang.compiler.bir.codegen.internal.AsyncDataCollector;
import org.wso2.ballerinalang.compiler.bir.codegen.internal.BIRVarToJVMIndexMap;
import org.wso2.ballerinalang.compiler.bir.codegen.internal.LabelGenerator;
import org.wso2.ballerinalang.compiler.bir.codegen.methodgen.InitMethodGen;
import org.wso2.ballerinalang.compiler.bir.codegen.model.CatchIns;
import org.wso2.ballerinalang.compiler.bir.codegen.model.JCast;
import org.wso2.ballerinalang.compiler.bir.codegen.model.JErrorEntry;
import org.wso2.ballerinalang.compiler.bir.codegen.model.JFieldBIRFunction;
import org.wso2.ballerinalang.compiler.bir.codegen.model.JIConstructorCall;
import org.wso2.ballerinalang.compiler.bir.codegen.model.JIMethodCall;
import org.wso2.ballerinalang.compiler.bir.codegen.model.JMethod;
import org.wso2.ballerinalang.compiler.bir.codegen.model.JMethodBIRFunction;
import org.wso2.ballerinalang.compiler.bir.codegen.model.JMethodKind;
import org.wso2.ballerinalang.compiler.bir.codegen.model.JType;
import org.wso2.ballerinalang.compiler.bir.codegen.model.JTypeTags;
import org.wso2.ballerinalang.compiler.bir.codegen.model.JavaField;
import org.wso2.ballerinalang.compiler.bir.codegen.split.JvmConstantsGen;
import org.wso2.ballerinalang.compiler.bir.model.BIRNode;
import org.wso2.ballerinalang.compiler.bir.model.BIRNode.BIRBasicBlock;
import org.wso2.ballerinalang.compiler.bir.model.BIRNode.BIRVariableDcl;
import org.wso2.ballerinalang.compiler.bir.model.BIROperand;
import org.wso2.ballerinalang.compiler.bir.model.BIRTerminator;
import org.wso2.ballerinalang.compiler.bir.model.VarKind;
import org.wso2.ballerinalang.compiler.semantics.analyzer.Types;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolTable;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.Symbols;
import org.wso2.ballerinalang.compiler.semantics.model.types.BArrayType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.util.Name;
import org.wso2.ballerinalang.compiler.util.TypeTags;
import org.wso2.ballerinalang.util.Flags;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.BASTORE;
import static org.objectweb.asm.Opcodes.CASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.F2D;
import static org.objectweb.asm.Opcodes.FASTORE;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.I2D;
import static org.objectweb.asm.Opcodes.I2L;
import static org.objectweb.asm.Opcodes.IASTORE;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.IF_ICMPGE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.L2D;
import static org.objectweb.asm.Opcodes.LASTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.SASTORE;
import static org.objectweb.asm.Opcodes.T_BOOLEAN;
import static org.objectweb.asm.Opcodes.T_CHAR;
import static org.objectweb.asm.Opcodes.T_DOUBLE;
import static org.objectweb.asm.Opcodes.T_FLOAT;
import static org.objectweb.asm.Opcodes.T_INT;
import static org.objectweb.asm.Opcodes.T_LONG;
import static org.objectweb.asm.Opcodes.T_SHORT;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.ARRAY_VALUE;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.ERROR_CODES;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.ERROR_HELPER;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.ERROR_REASONS;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.ERROR_VALUE;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.GET_VALUE_METHOD;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.HANDLE_VALUE;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.JVM_INIT_METHOD;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.OBJECT;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmConstants.STRING_VALUE;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmDesugarPhase.getNextDesugarBBId;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmDesugarPhase.insertAndGetNextBasicBlock;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.GET_BSTRING_FOR_ARRAY_INDEX;
import static org.wso2.ballerinalang.compiler.bir.codegen.JvmSignatures.GET_STRING_FROM_ARRAY;
/**
 * Interop related method generation class for JVM byte code generation.
 *
 * @since 1.2.0
 */
public final class InteropMethodGen {

    static void genJFieldForInteropField(JFieldBIRFunction birFunc, ClassWriter classWriter, PackageID birModule,
                                         JvmPackageGen jvmPackageGen, JvmTypeGen jvmTypeGen, JvmCastGen jvmCastGen,
                                         JvmConstantsGen jvmConstantsGen, AsyncDataCollector asyncDataCollector,
                                         Types types) {

        BIRVarToJVMIndexMap indexMap = new BIRVarToJVMIndexMap();
        indexMap.addIfNotExists("$_strand_$", jvmPackageGen.symbolTable.stringType);

        // Generate method desc
        BType retType = birFunc.type.retType;

        if (Symbols.isFlagOn(retType.getFlags(), Flags.PARAMETERIZED)) {
            retType = JvmCodeGenUtil.UNIFIER.build(types.typeEnv(), birFunc.type.retType);
        }

        String desc = JvmCodeGenUtil.getMethodDesc(types.typeEnv(), birFunc.type.paramTypes, retType);
        int access = birFunc.receiver != null ? ACC_PUBLIC : ACC_PUBLIC + ACC_STATIC;
        MethodVisitor mv = classWriter.visitMethod(access, birFunc.name.value, desc, null, null);
        JvmInstructionGen instGen = new JvmInstructionGen(mv, indexMap, birModule, jvmPackageGen, jvmTypeGen,
                                                          jvmCastGen, jvmConstantsGen, asyncDataCollector, types);
        JvmErrorGen errorGen = new JvmErrorGen(mv, indexMap, instGen);
        LabelGenerator labelGen = new LabelGenerator();
        JvmTerminatorGen termGen = new JvmTerminatorGen(mv, indexMap, labelGen, errorGen, birModule, instGen,
                jvmPackageGen, jvmTypeGen, jvmCastGen, asyncDataCollector);
        mv.visitCode();

        Label paramLoadLabel = labelGen.getLabel("param_load");
        mv.visitLabel(paramLoadLabel);
        mv.visitLineNumber(birFunc.pos.lineRange().startLine().line() + 1, paramLoadLabel);

        // all the function parameters from birFunc.localVars are extracted
        // the JVM method local variable index for each parameter is assigned
        List<BIRNode.BIRFunctionParameter> birFuncParams = new ArrayList<>();
        for (BIRVariableDcl birLocalVarOptional : birFunc.localVars) {
            if (birLocalVarOptional instanceof BIRNode.BIRFunctionParameter functionParameter) {
                birFuncParams.add(functionParameter);
                indexMap.addIfNotExists(functionParameter.name.value, functionParameter.type);
            }
        }

        JavaField jField = birFunc.javaField;
        JType jFieldType = JInterop.getJType(jField.getFieldType());

        // Load receiver which is the 0th parameter in the birFunc
        if (!jField.isStatic()) {
            BIRNode.BIRVariableDcl var = birFuncParams.getFirst();
            int receiverLocalVarIndex = indexMap.addIfNotExists(var.name.value, var.type);
            mv.visitVarInsn(ALOAD, receiverLocalVarIndex);
            mv.visitMethodInsn(INVOKEVIRTUAL, HANDLE_VALUE, GET_VALUE_METHOD, "()Ljava/lang/Object;", false);
            mv.visitTypeInsn(CHECKCAST, jField.getDeclaringClassName());

            Label ifNonNullLabel = labelGen.getLabel("receiver_null_check");
            mv.visitLabel(ifNonNullLabel);
            mv.visitInsn(DUP);

            Label elseBlockLabel = labelGen.getLabel("receiver_null_check_else");
            mv.visitJumpInsn(IFNONNULL, elseBlockLabel);
            Label thenBlockLabel = labelGen.getLabel("receiver_null_check_then");
            mv.visitLabel(thenBlockLabel);
            mv.visitFieldInsn(GETSTATIC, ERROR_REASONS, "JAVA_NULL_REFERENCE_ERROR", "L" + STRING_VALUE + ";");
            mv.visitFieldInsn(GETSTATIC, ERROR_CODES, "JAVA_NULL_REFERENCE", "L" + ERROR_CODES + ";");
            mv.visitInsn(ICONST_0);
            mv.visitTypeInsn(ANEWARRAY, OBJECT);
            mv.visitMethodInsn(INVOKESTATIC, ERROR_HELPER, "getRuntimeException",
                    "(L" + STRING_VALUE + ";L" + ERROR_CODES + ";[L" + OBJECT + ";)L" + ERROR_VALUE + ";", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(elseBlockLabel);
        }

        // Load java method parameters
        int birFuncParamIndex = jField.isStatic() ? 0 : 1;
        if (birFuncParamIndex < birFuncParams.size()) {
            BIRNode.BIRFunctionParameter birFuncParam = birFuncParams.get(birFuncParamIndex);
            int paramLocalVarIndex = indexMap.addIfNotExists(birFuncParam.name.value, birFuncParam.type);
            loadMethodParamToStackInInteropFunction(mv, birFuncParam, jFieldType,
                                                    paramLocalVarIndex, instGen, jvmCastGen);
        }

        if (jField.isStatic()) {
            if (jField.method == JFieldMethod.ACCESS) {
                mv.visitFieldInsn(GETSTATIC, jField.getDeclaringClassName(), jField.getName(), jField.getSignature());
            } else {
                mv.visitFieldInsn(PUTSTATIC, jField.getDeclaringClassName(), jField.getName(), jField.getSignature());
            }
        } else {
            if (jField.method == JFieldMethod.ACCESS) {
                mv.visitFieldInsn(GETFIELD, jField.getDeclaringClassName(), jField.getName(), jField.getSignature());
            } else {
                mv.visitFieldInsn(PUTFIELD, jField.getDeclaringClassName(), jField.getName(), jField.getSignature());
            }
        }

        // Handle return type
        BIRVariableDcl retVarDcl = new BIRVariableDcl(retType, new Name("$_ret_var_$"), null, VarKind.LOCAL);
        int returnVarRefIndex = indexMap.addIfNotExists(retVarDcl.name.value, retType);

        int retTypeTag = JvmCodeGenUtil.getImpliedType(retType).tag;
        if (retTypeTag == TypeTags.NIL) {
            mv.visitInsn(ACONST_NULL);
        } else if (retTypeTag == TypeTags.HANDLE) {
            // Here the corresponding Java method parameter type is 'jvm:RefType'. This has been verified before
            int returnJObjectVarRefIndex = indexMap.addIfNotExists("$_ret_jobject_var_$",
                                                                   jvmPackageGen.symbolTable.anyType);
            mv.visitVarInsn(ASTORE, returnJObjectVarRefIndex);
            mv.visitTypeInsn(NEW, HANDLE_VALUE);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, returnJObjectVarRefIndex);
            mv.visitMethodInsn(INVOKESPECIAL, HANDLE_VALUE, JVM_INIT_METHOD, "(Ljava/lang/Object;)V", false);
        } else {
            // bType is a value-type
            if (jField.getFieldType().isPrimitive() /*jFieldType instanceof JPrimitiveType*/) {
                performWideningPrimitiveConversion(mv, retType, jFieldType);
            } else {
                jvmCastGen.addUnboxInsn(mv, retType);
            }
        }

        instGen.generateVarStore(mv, retVarDcl, returnVarRefIndex);

        Label retLabel = labelGen.getLabel("return_lable");
        mv.visitLabel(retLabel);
        mv.visitLineNumber(birFunc.pos.lineRange().endLine().line() + 1, retLabel);
        termGen.genReturnTerm(returnVarRefIndex, birFunc, -1, -1, -1, -1);
        JvmCodeGenUtil.visitMaxStackForMethod(mv, birFunc.name.value, birFunc.javaField.getDeclaringClassName());
        mv.visitEnd();
    }

    public static void desugarInteropFuncs(Env typeEnv, JMethodBIRFunction birFunc, InitMethodGen initMethodGen) {
        // resetting the variable generation index
        BType retType = birFunc.type.retType;
        if (Symbols.isFlagOn(retType.getFlags(), Flags.PARAMETERIZED)) {
            retType = JvmCodeGenUtil.UNIFIER.build(typeEnv, birFunc.type.retType);
        }
        JMethod jMethod = birFunc.jMethod;
        Class<?>[] jMethodParamTypes = jMethod.getParamTypes();
        JType jMethodRetType = JInterop.getJType(jMethod.getReturnType());

        if (jMethodRetType == JType.J_VOID && jMethod.isBalEnvAcceptingMethod()) {
            jMethodRetType = JType.getJTypeForBType(birFunc.returnVariable.type);
        }

        initMethodGen.resetIds();
        BIRBasicBlock beginBB = insertAndGetNextBasicBlock(birFunc.basicBlocks, initMethodGen);
        BIROperand receiverOp = null;
        List<BIROperand> args = new ArrayList<>();
        List<BIROperand> resourcePathArgs = new ArrayList<>();
        List<BIROperand> functionArgs = new ArrayList<>();
        List<BIRNode.BIRFunctionParameter> birFuncParams = birFunc.parameters;

        int birFuncParamIndex = 0;
        int bReceiverParamIndex = 0;
        boolean isJavaInstanceMethod = jMethod.kind == JMethodKind.METHOD && !jMethod.isStatic();
        // Load receiver which is the 0th parameter in the birFunc
        if (isJavaInstanceMethod) {
            int pathParamCount = 0;
            for (BIRNode.BIRFunctionParameter birFuncParam : birFuncParams) {
                if (birFuncParam.isPathParameter) {
                    pathParamCount++;
                }
            }
            bReceiverParamIndex = pathParamCount;
            BIRNode.BIRFunctionParameter birFuncParam = birFuncParams.get(bReceiverParamIndex);
            BIROperand argRef = new BIROperand(birFuncParam);
            args.add(argRef);
        }

        JType varArgType = null;
        int jMethodParamIndex = 0;
        if (jMethod.getReceiverType() != null) {
            jMethodParamIndex++;
            receiverOp = new BIROperand(birFunc.receiver);
        }

        if (jMethod.isBalEnvAcceptingMethod()) {
            jMethodParamIndex++;
        }

        int paramCount = birFuncParams.size();
        while (birFuncParamIndex < paramCount) {
            if (birFuncParamIndex == bReceiverParamIndex && isJavaInstanceMethod) {
                birFuncParamIndex++;
                continue;
            }
            BIRNode.BIRFunctionParameter birFuncParam = birFuncParams.get(birFuncParamIndex);
            BType bPType = birFuncParam.type;
            BIROperand argRef = new BIROperand(birFuncParam);
            boolean isVarArg = (birFuncParamIndex == (paramCount - 1)) && birFunc.restParam != null;
            if (jMethod.hasBundledPathParams && birFuncParam.isPathParameter) {
                if (resourcePathArgs.isEmpty()) {
                    jMethodParamIndex++;
                }
                resourcePathArgs.add(argRef);
                birFuncParamIndex++;
                continue;
            }
            if (jMethod.hasBundledFunctionParams && !birFuncParam.isPathParameter) {
                if (functionArgs.isEmpty()) {
                    jMethodParamIndex++;
                }
                functionArgs.add(argRef);
                birFuncParamIndex++;
                continue;
            }

            JType jPType = JInterop.getJType(jMethodParamTypes[jMethodParamIndex]);
            // we generate cast operations for unmatching B to J types
            if (!isVarArg && !isMatchingBAndJType(bPType, jPType)) {
                String varName = "$_param_jobject_var" + birFuncParamIndex + "_$";
                BIRVariableDcl paramVarDcl = new BIRVariableDcl(jPType, new Name(varName), null, VarKind.LOCAL);
                birFunc.localVars.add(paramVarDcl);
                BIROperand paramVarRef = new BIROperand(paramVarDcl);
                JCast jToBCast = new JCast(birFunc.pos);
                jToBCast.lhsOp = paramVarRef;
                jToBCast.rhsOp = argRef;
                jToBCast.targetType = jPType;
                argRef = paramVarRef;
                beginBB.instructions.add(jToBCast);
            }
            // for var args, we have two options
            // 1 - desugar java array creation here,
            // 2 - keep the var arg type in the instruction and do the array creation in instruction gen
            // we are going with the option two for the time being, hence keeping var arg type in the instructions
            // (drawback with option 2 is, function frame may not have proper variables)
            if (isVarArg) {
                varArgType = jPType;
            }
            args.add(argRef);
            birFuncParamIndex += 1;
            jMethodParamIndex += 1;
        }

        int invocationType = INVOKESTATIC;
        if (jMethod.kind == JMethodKind.METHOD && !jMethod.isStatic()) {
            if (jMethod.isDeclaringClassInterface()) {
                invocationType = INVOKEINTERFACE;
            } else {
                invocationType = INVOKEVIRTUAL;
            }
        } else if (!(jMethod.kind == JMethodKind.METHOD && jMethod.isStatic())) {
            invocationType = INVOKESPECIAL;
        }

        BIROperand jRetVarRef = null;

        BIRBasicBlock thenBB = insertAndGetNextBasicBlock(birFunc.basicBlocks, initMethodGen);
        BIRBasicBlock retBB = new BIRBasicBlock(getNextDesugarBBId(initMethodGen));
        thenBB.terminator = new BIRTerminator.GOTO(birFunc.pos, retBB);
        BIROperand retRef = new BIROperand(birFunc.localVars.getFirst());

        if (JvmCodeGenUtil.getImpliedType(retType).tag != TypeTags.NIL) {
            if (JType.J_VOID != jMethodRetType) {
                BIRVariableDcl retJObjectVarDcl = new BIRVariableDcl(jMethodRetType, new Name("$_ret_jobject_var_$"),
                        null, VarKind.LOCAL);
                birFunc.localVars.add(retJObjectVarDcl);
                BIROperand castVarRef = new BIROperand(retJObjectVarDcl);
                jRetVarRef = castVarRef;
                JCast jToBCast = new JCast(birFunc.pos);
                jToBCast.lhsOp = retRef;
                jToBCast.rhsOp = castVarRef;
                jToBCast.targetType = retType;
                thenBB.instructions.add(jToBCast);
            }
        }
        BIRBasicBlock catchBB = new BIRBasicBlock(getNextDesugarBBId(initMethodGen));
        JErrorEntry ee = new JErrorEntry(beginBB, thenBB, retRef, catchBB);
        for (Class<?> exception : birFunc.jMethod.getExceptionTypes()) {
            BIRTerminator.Return exceptionRet = new BIRTerminator.Return(birFunc.pos);
            CatchIns catchIns = new CatchIns();
            catchIns.errorClass = exception.getName().replace(".", "/");
            catchIns.term = exceptionRet;
            ee.catchIns.add(catchIns);
        }
        birFunc.errorTable.add(ee);

        // We may be able to use the same instruction rather than two, check later
        if (jMethod.kind == JMethodKind.CONSTRUCTOR) {
            JIConstructorCall jCall = new JIConstructorCall(birFunc.pos);
            jCall.args = args;
            jCall.receiver = receiverOp;
            jCall.resourcePathArgs = resourcePathArgs;
            jCall.functionArgs = functionArgs;
            jCall.varArgExist = birFunc.restParam != null;
            jCall.varArgType = varArgType;
            jCall.lhsOp = jRetVarRef;
            jCall.jClassName = jMethod.getClassName().replace(".", "/");
            jCall.name = jMethod.getName();
            jCall.jMethodVMSig = jMethod.getSignature();
            jCall.thenBB = thenBB;
            beginBB.terminator = jCall;
        } else {
            JIMethodCall jCall = new JIMethodCall(birFunc.pos);
            jCall.args = args;
            jCall.receiver = receiverOp;
            jCall.resourcePathArgs = resourcePathArgs;
            jCall.functionArgs = functionArgs;
            jCall.varArgExist = birFunc.restParam != null;
            jCall.varArgType = varArgType;
            jCall.lhsOp = jRetVarRef;
            jCall.jClassName = jMethod.getClassName().replace(".", "/");
            jCall.name = jMethod.getName();
            jCall.jMethodVMSig = jMethod.getSignature();
            jCall.invocationType = invocationType;
            jCall.thenBB = thenBB;
            beginBB.terminator = jCall;
        }

        // Adding the returnBB to the end of BB list
        birFunc.basicBlocks.add(retBB);

        retBB.terminator = new BIRTerminator.Return(birFunc.pos);
    }

    private static boolean isMatchingBAndJType(BType sourceType, JType targetType) {
        int sourceTypeTag = JvmCodeGenUtil.getImpliedType(sourceType).tag;
        return (TypeTags.isIntegerTypeTag(sourceTypeTag) && targetType.jTag == JTypeTags.JLONG) ||
                (sourceTypeTag == TypeTags.FLOAT && targetType.jTag == JTypeTags.JDOUBLE) ||
                (sourceTypeTag == TypeTags.BOOLEAN && targetType.jTag == JTypeTags.JBOOLEAN);
    }

    // These conversions are already validate beforehand, therefore I am just emitting type conversion instructions
    // here. We can improve following logic with a type lattice.
    private static void performWideningPrimitiveConversion(MethodVisitor mv, BType bType, JType jType) {
        int typeTag = JvmCodeGenUtil.getImpliedType(bType).tag;
        if (!TypeTags.isIntegerTypeTag(typeTag) || jType.jTag != JTypeTags.JLONG) {
            if (typeTag != TypeTags.FLOAT || jType.jTag != JTypeTags.JDOUBLE) {
                if (TypeTags.isIntegerTypeTag(typeTag)) {
                    mv.visitInsn(I2L);
                } else if (typeTag == TypeTags.FLOAT) {
                    if (jType.jTag == JTypeTags.JLONG) {
                        mv.visitInsn(L2D);
                    } else if (jType.jTag == JTypeTags.JFLOAT) {
                        mv.visitInsn(F2D);
                    } else {
                        mv.visitInsn(I2D);
                    }
                }
            }
        }
    }

    private static void loadMethodParamToStackInInteropFunction(
            MethodVisitor mv, BIRNode.BIRFunctionParameter birFuncParam, JType jMethodParamType, int localVarIndex,
            JvmInstructionGen jvmInstructionGen, JvmCastGen jvmCastGen) {

        BType bFuncParamType = birFuncParam.type;
        // Load the parameter value to the stack
        jvmInstructionGen.generateVarLoad(mv, birFuncParam, localVarIndex);
        jvmCastGen.generateBToJCheckCast(mv, bFuncParamType, jMethodParamType);
    }

    public static String getJTypeSignature(JType jType) {

        if (jType.jTag == JTypeTags.JREF) {
            return "L" + ((JType.JRefType) jType).typeValue + ";";
        } else if (jType.jTag == JTypeTags.JARRAY) {
            JType eType = ((JType.JArrayType) jType).elementType;
            return "[" + getJTypeSignature(eType);
        }

        return switch (jType.jTag) {
            case JTypeTags.JBYTE -> "B";
            case JTypeTags.JCHAR -> "C";
            case JTypeTags.JSHORT -> "S";
            case JTypeTags.JINT -> "I";
            case JTypeTags.JLONG -> "J";
            case JTypeTags.JFLOAT -> "F";
            case JTypeTags.JDOUBLE -> "D";
            case JTypeTags.JBOOLEAN -> "Z";
            default -> throw new BLangCompilerException("invalid element type: " + jType);
        };
    }

    public static String getSignatureForJType(JType jType) {
        if (jType.jTag == JTypeTags.JREF) {
            return ((JType.JRefType) jType).typeValue;
        } else if (jType.jTag == JTypeTags.JARRAY) { //must be JArrayType
            JType eType = ((JType.JArrayType) jType).elementType;
            StringBuilder sig = new StringBuilder("[");
            while (eType.jTag == JTypeTags.JARRAY) {
                eType = ((JType.JArrayType) eType).elementType;
                sig.append("[");
            }

            return switch (eType.jTag) {
                case JTypeTags.JREF -> sig + "L" + getSignatureForJType(eType) + ";";
                case JTypeTags.JBYTE -> sig + "B";
                case JTypeTags.JCHAR -> sig + "C";
                case JTypeTags.JSHORT -> sig + "S";
                case JTypeTags.JINT -> sig + "I";
                case JTypeTags.JLONG -> sig + "J";
                case JTypeTags.JFLOAT -> sig + "F";
                case JTypeTags.JDOUBLE -> sig + "D";
                case JTypeTags.JBOOLEAN -> sig + "Z";
                default -> throw new BLangCompilerException("invalid element type: " + eType);
            };
        } else {
            throw new BLangCompilerException("invalid element type: " + jType);
        }
    }

    public static void genVarArg(MethodVisitor mv, BIRVarToJVMIndexMap indexMap, BType bType, JType jvmType,
                                 int varArgIndex, SymbolTable symbolTable, JvmCastGen jvmCastGen) {

        JType jElementType;
        BType bElementType;
        bType = JvmCodeGenUtil.getImpliedType(bType);
        if (jvmType.jTag == JTypeTags.JARRAY && bType.tag == TypeTags.ARRAY) {
            jElementType = ((JType.JArrayType) jvmType).elementType;
            bElementType = ((BArrayType) bType).eType;
        } else {
            throw new BLangCompilerException("invalid type for var-arg: " + jvmType);
        }

        int varArgsLenVarIndex = indexMap.addIfNotExists("$varArgsLen", symbolTable.intType);
        int indexVarIndex = indexMap.addIfNotExists("$index", symbolTable.intType);
        int valueArrayIndex = indexMap.addIfNotExists("$valueArray", symbolTable.anyType);

        // get the number of var args provided
        mv.visitVarInsn(ALOAD, varArgIndex);
        mv.visitMethodInsn(INVOKEINTERFACE, ARRAY_VALUE, "size", "()I", true);
        mv.visitInsn(DUP);  // duplicate array size - needed for array new
        mv.visitVarInsn(ISTORE, varArgsLenVarIndex);

        // create an array to hold the results. i.e: jvm values
        genArrayNew(mv, jElementType);
        mv.visitVarInsn(ASTORE, valueArrayIndex);

        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, indexVarIndex);
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitLabel(l1);

        // if index >= varArgsLen, then jump to end
        mv.visitVarInsn(ILOAD, indexVarIndex);
        mv.visitVarInsn(ILOAD, varArgsLenVarIndex);
        mv.visitJumpInsn(IF_ICMPGE, l2);

        // `valueArray` and `index` to stack, for lhs of assignment
        mv.visitVarInsn(ALOAD, valueArrayIndex);
        mv.visitVarInsn(ILOAD, indexVarIndex);

        // load `varArg[index]`
        mv.visitVarInsn(ALOAD, varArgIndex);
        mv.visitVarInsn(ILOAD, indexVarIndex);
        mv.visitInsn(I2L);
        
        int elementTypeTag = JvmCodeGenUtil.getImpliedType(bElementType).tag;
        if (TypeTags.isIntegerTypeTag(elementTypeTag)) {
            mv.visitMethodInsn(INVOKEINTERFACE, ARRAY_VALUE, "getInt", "(J)J", true);
        } else if (TypeTags.isStringTypeTag(elementTypeTag)) {
            mv.visitMethodInsn(INVOKEINTERFACE, ARRAY_VALUE, "getBString",
                               GET_BSTRING_FOR_ARRAY_INDEX, true);
        } else {
            switch (elementTypeTag) {
                case TypeTags.BOOLEAN -> mv.visitMethodInsn(INVOKEINTERFACE, ARRAY_VALUE, "getBoolean", "(J)Z", true);
                case TypeTags.BYTE -> mv.visitMethodInsn(INVOKEINTERFACE, ARRAY_VALUE, "getByte", "(J)B", true);
                case TypeTags.FLOAT -> mv.visitMethodInsn(INVOKEINTERFACE, ARRAY_VALUE, "getFloat", "(J)D", true);
                case TypeTags.HANDLE -> {
                    mv.visitMethodInsn(INVOKEINTERFACE, ARRAY_VALUE, "getRefValue", GET_STRING_FROM_ARRAY,
                            true);
                    mv.visitTypeInsn(CHECKCAST, HANDLE_VALUE);
                }
                default -> mv.visitMethodInsn(INVOKEINTERFACE, ARRAY_VALUE, "getRefValue", GET_STRING_FROM_ARRAY,
                        true);
            }
        }

        // unwrap from handleValue
        jvmCastGen.generateBToJCheckCast(mv, bElementType, jElementType);

        // valueArray[index] = varArg[index]
        genArrayStore(mv, jElementType);

        // // increment index, and go to the condition again
        mv.visitIincInsn(indexVarIndex, 1);
        mv.visitJumpInsn(GOTO, l1);

        mv.visitLabel(l2);
        mv.visitVarInsn(ALOAD, valueArrayIndex);
    }

    private static void genArrayStore(MethodVisitor mv, JType jType) {

        int code = switch (jType.jTag) {
            case JTypeTags.JINT -> IASTORE;
            case JTypeTags.JLONG -> LASTORE;
            case JTypeTags.JDOUBLE -> DASTORE;
            case JTypeTags.JBYTE, JTypeTags.JBOOLEAN -> BASTORE;
            case JTypeTags.JSHORT -> SASTORE;
            case JTypeTags.JCHAR -> CASTORE;
            case JTypeTags.JFLOAT -> FASTORE;
            default -> AASTORE;
        };

        mv.visitInsn(code);
    }

    private static void genArrayNew(MethodVisitor mv, JType elementType) {

        switch (elementType.jTag) {
            case JTypeTags.JINT -> mv.visitIntInsn(NEWARRAY, T_INT);
            case JTypeTags.JLONG -> mv.visitIntInsn(NEWARRAY, T_LONG);
            case JTypeTags.JDOUBLE -> mv.visitIntInsn(NEWARRAY, T_DOUBLE);
            case JTypeTags.JBYTE, JTypeTags.JBOOLEAN -> mv.visitIntInsn(NEWARRAY, T_BOOLEAN);
            case JTypeTags.JSHORT -> mv.visitIntInsn(NEWARRAY, T_SHORT);
            case JTypeTags.JCHAR -> mv.visitIntInsn(NEWARRAY, T_CHAR);
            case JTypeTags.JFLOAT -> mv.visitIntInsn(NEWARRAY, T_FLOAT);
            case JTypeTags.JREF, JTypeTags.JARRAY -> mv.visitTypeInsn(ANEWARRAY, getSignatureForJType(elementType));
            default -> throw new BLangCompilerException("invalid type for var-arg: " + elementType);
        }
    }

    private InteropMethodGen() {
    }
}
