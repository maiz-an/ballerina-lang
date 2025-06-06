/*
 *  Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.ballerinalang.compiler.tree;

import org.ballerinalang.model.tree.Node;
import org.wso2.ballerinalang.compiler.tree.bindingpatterns.BLangCaptureBindingPattern;
import org.wso2.ballerinalang.compiler.tree.bindingpatterns.BLangErrorBindingPattern;
import org.wso2.ballerinalang.compiler.tree.bindingpatterns.BLangErrorCauseBindingPattern;
import org.wso2.ballerinalang.compiler.tree.bindingpatterns.BLangErrorFieldBindingPatterns;
import org.wso2.ballerinalang.compiler.tree.bindingpatterns.BLangErrorMessageBindingPattern;
import org.wso2.ballerinalang.compiler.tree.bindingpatterns.BLangFieldBindingPattern;
import org.wso2.ballerinalang.compiler.tree.bindingpatterns.BLangListBindingPattern;
import org.wso2.ballerinalang.compiler.tree.bindingpatterns.BLangMappingBindingPattern;
import org.wso2.ballerinalang.compiler.tree.bindingpatterns.BLangNamedArgBindingPattern;
import org.wso2.ballerinalang.compiler.tree.bindingpatterns.BLangRestBindingPattern;
import org.wso2.ballerinalang.compiler.tree.bindingpatterns.BLangSimpleBindingPattern;
import org.wso2.ballerinalang.compiler.tree.bindingpatterns.BLangWildCardBindingPattern;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangCollectClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangDoClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangFromClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangGroupByClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangGroupingKey;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangInputClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangJoinClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangLetClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangLimitClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangMatchClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangOnClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangOnConflictClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangOnFailClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangOrderByClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangOrderKey;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangSelectClause;
import org.wso2.ballerinalang.compiler.tree.clauses.BLangWhereClause;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangAccessExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangAlternateWorkerReceive;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangAnnotAccessExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangArrowFunction;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangBinaryExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangCheckPanickedExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangCheckedExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangCollectContextInvocation;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangCommitExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangConstRef;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangConstant;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangDynamicArgExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangElvisExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangErrorConstructorExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangErrorVarRef;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangExtendedXMLNavigationAccess;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangFieldBasedAccess;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangGroupExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangIgnoreExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangIndexBasedAccess;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangInferredTypedescDefaultNode;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangInvocation;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangIsAssignableExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangIsLikeExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangLambdaFunction;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangLetExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangListConstructorExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangMarkDownDeprecatedParametersDocumentation;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangMarkDownDeprecationDocumentation;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangMarkdownDocumentationLine;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangMarkdownParameterDocumentation;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangMarkdownReturnParameterDocumentation;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangMatchGuard;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangMultipleWorkerReceive;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangNamedArgsExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangNaturalExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangObjectConstructorExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangQueryAction;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangQueryExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRawTemplateLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangReAssertion;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangReAtomCharOrEscape;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangReAtomQuantifier;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangReCapturingGroups;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangReCharSet;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangReCharSetRange;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangReCharacterClass;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangReDisjunction;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangReFlagExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangReFlagsOnOff;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangReQuantifier;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangReSequence;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRecordLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRecordVarRef;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRegExpTemplateLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangRestArgsExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangServiceConstructorExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangSimpleVarRef;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangStatementExpression;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangStringTemplateLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTableConstructorExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTernaryExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTransactionalExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTrapExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTupleVarRef;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTypeConversionExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTypeInit;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTypeTestExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangTypedescExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangUnaryExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangVariableReference;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangWaitExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangWaitForAllExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangWorkerAsyncSendExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangWorkerFlushExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangWorkerReceive;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangWorkerSyncSendExpr;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLAttribute;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLCommentLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLElementAccess;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLElementFilter;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLElementLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLFilterStepExtend;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLIndexedStepExtend;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLMethodCallStepExtend;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLNavigationAccess;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLProcInsLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLQName;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLQuotedString;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLSequenceLiteral;
import org.wso2.ballerinalang.compiler.tree.expressions.BLangXMLTextLiteral;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangConstPattern;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangErrorCauseMatchPattern;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangErrorFieldMatchPatterns;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangErrorMatchPattern;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangErrorMessageMatchPattern;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangFieldMatchPattern;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangListMatchPattern;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangMappingMatchPattern;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangMatchPattern;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangNamedArgMatchPattern;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangRestMatchPattern;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangSimpleMatchPattern;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangVarBindingPatternMatchPattern;
import org.wso2.ballerinalang.compiler.tree.matchpatterns.BLangWildCardMatchPattern;
import org.wso2.ballerinalang.compiler.tree.statements.BLangAssignment;
import org.wso2.ballerinalang.compiler.tree.statements.BLangBlockStmt;
import org.wso2.ballerinalang.compiler.tree.statements.BLangBreak;
import org.wso2.ballerinalang.compiler.tree.statements.BLangCompoundAssignment;
import org.wso2.ballerinalang.compiler.tree.statements.BLangContinue;
import org.wso2.ballerinalang.compiler.tree.statements.BLangDo;
import org.wso2.ballerinalang.compiler.tree.statements.BLangErrorDestructure;
import org.wso2.ballerinalang.compiler.tree.statements.BLangErrorVariableDef;
import org.wso2.ballerinalang.compiler.tree.statements.BLangExpressionStmt;
import org.wso2.ballerinalang.compiler.tree.statements.BLangFail;
import org.wso2.ballerinalang.compiler.tree.statements.BLangForeach;
import org.wso2.ballerinalang.compiler.tree.statements.BLangForkJoin;
import org.wso2.ballerinalang.compiler.tree.statements.BLangIf;
import org.wso2.ballerinalang.compiler.tree.statements.BLangLock;
import org.wso2.ballerinalang.compiler.tree.statements.BLangMatchStatement;
import org.wso2.ballerinalang.compiler.tree.statements.BLangPanic;
import org.wso2.ballerinalang.compiler.tree.statements.BLangRecordDestructure;
import org.wso2.ballerinalang.compiler.tree.statements.BLangRecordVariableDef;
import org.wso2.ballerinalang.compiler.tree.statements.BLangRetry;
import org.wso2.ballerinalang.compiler.tree.statements.BLangRetryTransaction;
import org.wso2.ballerinalang.compiler.tree.statements.BLangReturn;
import org.wso2.ballerinalang.compiler.tree.statements.BLangRollback;
import org.wso2.ballerinalang.compiler.tree.statements.BLangSimpleVariableDef;
import org.wso2.ballerinalang.compiler.tree.statements.BLangTransaction;
import org.wso2.ballerinalang.compiler.tree.statements.BLangTupleDestructure;
import org.wso2.ballerinalang.compiler.tree.statements.BLangTupleVariableDef;
import org.wso2.ballerinalang.compiler.tree.statements.BLangWhile;
import org.wso2.ballerinalang.compiler.tree.statements.BLangXMLNSStatement;
import org.wso2.ballerinalang.compiler.tree.types.BLangArrayType;
import org.wso2.ballerinalang.compiler.tree.types.BLangBuiltInRefTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangConstrainedType;
import org.wso2.ballerinalang.compiler.tree.types.BLangErrorType;
import org.wso2.ballerinalang.compiler.tree.types.BLangFiniteTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangFunctionTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangIntersectionTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangLetVariable;
import org.wso2.ballerinalang.compiler.tree.types.BLangObjectTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangRecordTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangStreamType;
import org.wso2.ballerinalang.compiler.tree.types.BLangStructureTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangTableTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangTupleTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangUnionTypeNode;
import org.wso2.ballerinalang.compiler.tree.types.BLangUserDefinedType;
import org.wso2.ballerinalang.compiler.tree.types.BLangValueType;

import java.util.List;


/**
 * The {@link SimpleBLangNodeAnalyzer} transforms each {@link BLangNode} objects to another object of type T.
 * <p>
 * This is simplified node visitor of the {@link BLangNodeAnalyzer}.
 *
 * @param <T> the type of data class that passed along with transform methods.
 * @since 2.0.0
 */
public abstract class SimpleBLangNodeAnalyzer<T> extends BLangNodeAnalyzer<T> {

    public abstract void analyzeNode(BLangNode node, T data);

    public void visitNode(BLangNode node, T data) {
        if (node == null) {
            return;
        }
        node.accept(this, data);
    }

    public void visitNode(List<? extends Node> nodes, T data) {
        if (nodes == null) {
            return;
        }
        for (Node node : nodes) {
            visitNode((BLangNode) node, data);
        }
    }

    // Base Nodes

    @Override
    public void visit(BLangAnnotation node, T data) {
        analyzeNode(node, data);
        visitNode(node.name, data);
        visitNode(node.annAttachments, data);
        visitNode(node.markdownDocumentationAttachment, data);
        visitNode(node.typeNode, data);
    }

    @Override
    public void visit(BLangAnnotationAttachment node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
        visitNode(node.annotationName, data);
        visitNode(node.pkgAlias, data);
    }

    @Override
    public void visit(BLangBlockFunctionBody node, T data) {
        analyzeNode(node, data);
        visitNode(node.stmts, data);
    }

    @Override
    public void visit(BLangClassDefinition node, T data) {
        analyzeNode(node, data);
        visitNode(node.name, data);
        visitNode(node.annAttachments, data);
        visitNode(node.markdownDocumentationAttachment, data);
        visitNode(node.initFunction, data);
        visitNode(node.functions, data);
        visitNode(node.fields, data);
        visitNode(node.typeRefs, data);
    }

    @Override
    public void visit(BLangCompilationUnit node, T data) {
        analyzeNode(node, data);
        visitNode(node.topLevelNodes, data);
    }

    @Override
    public void visit(BLangErrorVariable node, T data) {
        analyzeNode(node, data);
        visitBLangVariableNode(node, data);
        visitNode(node.message, data);
        visitNode(node.cause, data);
        visitNode(node.restDetail, data);
        visitNode(node.detail, data);
    }

    @Override
    public void visit(BLangErrorVariable.BLangErrorDetailEntry node, T data) {
        analyzeNode(node, data);
        visitNode(node.key, data);
        visitNode(node.valueBindingPattern, data);
    }

    @Override
    public void visit(BLangExprFunctionBody node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangExternalFunctionBody node, T data) {
        analyzeNode(node, data);
        visitNode(node.annAttachments, data);
    }

    @Override
    public void visit(BLangFunction node, T data) {
        analyzeNode(node, data);
        visitBLangInvokableNode(node, data);
    }

    @Override
    public void visit(BLangIdentifier node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangImportPackage node, T data) {
        analyzeNode(node, data);
        visitNode(node.orgName, data);
        visitNode(node.pkgNameComps, data);
        visitNode(node.alias, data);
        visitNode(node.compUnit, data);
        visitNode(node.version, data);
    }

    @Override
    public void visit(BLangMarkdownDocumentation node, T data) {
        analyzeNode(node, data);
        visitNode(node.documentationLines, data);
        visitNode(node.parameters, data);
        visitNode(node.references, data);
        visitNode(node.returnParameter, data);
        visitNode(node.deprecationDocumentation, data);
        visitNode(node.deprecatedParametersDocumentation, data);
    }

    @Override
    public void visit(BLangMarkdownReferenceDocumentation node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public abstract void visit(BLangPackage node, T data);

    @Override
    public void visit(BLangRecordVariable node, T data) {
        analyzeNode(node, data);
        visitBLangVariableNode(node, data);
        visitNode(node.variableList, data);
        visitNode(node.restParam, data);
    }

    @Override
    public void visit(BLangRecordVariable.BLangRecordVariableKeyValue node, T data) {
        analyzeNode(node, data);
        visitNode(node.key, data);
        visitNode(node.valueBindingPattern, data);
    }

    @Override
    public void visit(BLangResourceFunction node, T data) {
        analyzeNode(node, data);
        visit((BLangFunction) node, data);
        visitNode(node.methodName, data);
        visitNode(node.resourcePathSegments, data);
        visitNode(node.restPathParam, data);
        visitNode(node.pathParams, data);
    }

    @Override
    public void visit(BLangResourcePathSegment node, T data) {
        analyzeNode(node, data);
        visitNode(node.name, data);
        visitNode(node.typeNode, data);
    }
    
    @Override
    public void visit(BLangRetrySpec node, T data) {
        analyzeNode(node, data);
        visitNode(node.retryManagerType, data);
        visitNode(node.argExprs, data);
    }

    @Override
    public void visit(BLangService node, T data) {
        analyzeNode(node, data);
        visitNode(node.serviceVariable, data);
        visitNode(node.attachedExprs, data);
        visitNode(node.serviceClass, data);
        visitNode(node.absoluteResourcePath, data);
        visitNode(node.serviceNameLiteral, data);
        visitNode(node.name, data);
        visitNode(node.annAttachments, data);
        visitNode(node.markdownDocumentationAttachment, data);
    }

    @Override
    public void visit(BLangSimpleVariable node, T data) {
        analyzeNode(node, data);
        visitBLangVariableNode(node, data);
        visitNode(node.name, data);
    }

    @Override
    public void visit(BLangTableKeySpecifier node, T data) {
        analyzeNode(node, data);
        visitNode(node.fieldNameIdentifierList, data);
    }

    @Override
    public void visit(BLangTableKeyTypeConstraint node, T data) {
        analyzeNode(node, data);
        visitNode(node.keyType, data);
    }

    @Override
    public void visit(BLangTestablePackage node, T data) {
        analyzeNode(node, data);
        visit((BLangPackage) node, data);
    }

    @Override
    public void visit(BLangTupleVariable node, T data) {
        analyzeNode(node, data);
        visitBLangVariableNode(node, data);
        visitNode(node.memberVariables, data);
        visitNode(node.restVariable, data);
    }

    @Override
    public void visit(BLangTypeDefinition node, T data) {
        analyzeNode(node, data);
        visitNode(node.name, data);
        visitNode(node.typeNode, data);
        visitNode(node.annAttachments, data);
        visitNode(node.markdownDocumentationAttachment, data);
    }

    @Override
    public void visit(BLangXMLNS node, T data) {
        analyzeNode(node, data);
        visitNode(node.namespaceURI, data);
        visitNode(node.prefix, data);
    }

    @Override
    public void visit(BLangXMLNS.BLangLocalXMLNS node, T data) {
        analyzeNode(node, data);
        visit((BLangXMLNS) node, data);
    }

    @Override
    public void visit(BLangXMLNS.BLangPackageXMLNS node, T data) {
        analyzeNode(node, data);
        visit((BLangXMLNS) node, data);
    }

    // Binding-patterns

    @Override
    public void visit(BLangCaptureBindingPattern node, T data) {
        analyzeNode(node, data);
        visitNode(node.identifier, data);
    }

    @Override
    public void visit(BLangErrorBindingPattern node, T data) {
        analyzeNode(node, data);
        visitNode(node.errorTypeReference, data);
        visitNode(node.errorMessageBindingPattern, data);
        visitNode(node.errorCauseBindingPattern, data);
        visitNode(node.errorFieldBindingPatterns, data);
    }

    @Override
    public void visit(BLangErrorCauseBindingPattern node, T data) {
        analyzeNode(node, data);
        visitNode(node.simpleBindingPattern, data);
        visitNode(node.errorBindingPattern, data);
    }

    @Override
    public void visit(BLangErrorFieldBindingPatterns node, T data) {
        analyzeNode(node, data);
        visitNode(node.namedArgBindingPatterns, data);
        visitNode(node.restBindingPattern, data);
    }

    @Override
    public void visit(BLangErrorMessageBindingPattern node, T data) {
        analyzeNode(node, data);
        visitNode(node.simpleBindingPattern, data);
    }

    @Override
    public void visit(BLangFieldBindingPattern node, T data) {
        analyzeNode(node, data);
        visitNode(node.fieldName, data);
        visitNode(node.bindingPattern, data);
    }

    @Override
    public void visit(BLangListBindingPattern node, T data) {
        analyzeNode(node, data);
        visitNode(node.bindingPatterns, data);
        visitNode(node.restBindingPattern, data);
    }

    @Override
    public void visit(BLangMappingBindingPattern node, T data) {
        analyzeNode(node, data);
        visitNode(node.fieldBindingPatterns, data);
        visitNode(node.restBindingPattern, data);
    }

    @Override
    public void visit(BLangNamedArgBindingPattern node, T data) {
        analyzeNode(node, data);
        visitNode(node.argName, data);
        visitNode(node.bindingPattern, data);
    }

    @Override
    public void visit(BLangRestBindingPattern node, T data) {
        analyzeNode(node, data);
        visitNode(node.variableName, data);
    }

    @Override
    public void visit(BLangSimpleBindingPattern node, T data) {
        analyzeNode(node, data);
        visitNode(node.captureBindingPattern, data);
        visitNode(node.wildCardBindingPattern, data);
    }

    @Override
    public void visit(BLangWildCardBindingPattern node, T data) {
        analyzeNode(node, data);
    }

    // Clauses

    @Override
    public void visit(BLangDoClause node, T data) {
        analyzeNode(node, data);
        visitNode(node.body, data);
    }

    @Override
    public void visit(BLangFromClause node, T data) {
        analyzeNode(node, data);
        visitBLangInputClause(node, data);
    }

    @Override
    public void visit(BLangJoinClause node, T data) {
        analyzeNode(node, data);
        visitBLangInputClause(node, data);
        visitNode(node.onClause, data);
    }

    @Override
    public void visit(BLangLetClause node, T data) {
        analyzeNode(node, data);
        visitNode(node.letVarDeclarations, data);
    }

    @Override
    public void visit(BLangLimitClause node, T data) {
        analyzeNode(node, data);
        visitNode(node.expression, data);
    }

    @Override
    public void visit(BLangMatchClause node, T data) {
        analyzeNode(node, data);
        visitNode(node.matchPatterns, data);
        visitNode(node.expr, data);
        visitNode(node.blockStmt, data);
        visitNode(node.matchGuard, data);
    }

    @Override
    public void visit(BLangOnClause node, T data) {
        analyzeNode(node, data);
        visitNode(node.lhsExpr, data);
        visitNode(node.rhsExpr, data);
    }

    @Override
    public void visit(BLangOnConflictClause node, T data) {
        analyzeNode(node, data);
        visitNode(node.expression, data);
    }

    @Override
    public void visit(BLangOnFailClause node, T data) {
        analyzeNode(node, data);
        visitNode(node.body, data);
        visitNode((BLangNode) node.variableDefinitionNode, data);
    }

    @Override
    public void visit(BLangOrderByClause node, T data) {
        analyzeNode(node, data);
        visitNode(node.orderByKeyList, data);
    }

    @Override
    public void visit(BLangGroupByClause node, T data) {
        analyzeNode(node, data);
        visitNode(node.groupingKeyList, data);
    }

    @Override
    public void visit(BLangGroupingKey node, T data) {
        analyzeNode(node, data);
        visitNode(node.variableRef, data);
        visitNode(node.variableDef, data);
    }

    @Override
    public void visit(BLangOrderKey node, T data) {
        analyzeNode(node, data);
        visitNode(node.expression, data);
    }

    @Override
    public void visit(BLangSelectClause node, T data) {
        analyzeNode(node, data);
        visitNode(node.expression, data);
    }

    @Override
    public void visit(BLangCollectClause node, T data) {
        analyzeNode(node, data);
        visitNode(node.expression, data);
    }

    @Override
    public void visit(BLangWhereClause node, T data) {
        analyzeNode(node, data);
        visitNode(node.expression, data);
    }

    // Expressions

    @Override
    public void visit(BLangAnnotAccessExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
        visitNode(node.pkgAlias, data);
        visitNode(node.annotationName, data);
    }

    @Override
    public void visit(BLangArrowFunction node, T data) {
        analyzeNode(node, data);
        visitNode(node.params, data);
        visitNode((BLangNode) node.functionName, data);
        visitNode(node.body, data);
    }

    @Override
    public void visit(BLangBinaryExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.lhsExpr, data);
        visitNode(node.rhsExpr, data);
    }

    @Override
    public void visit(BLangCheckedExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangCheckPanickedExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangCommitExpr node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangConstant node, T data) {
        analyzeNode(node, data);
        visitBLangVariableNode(node, data);
        visitNode(node.name, data);
        visitNode(node.associatedTypeDefinition, data);
    }

    @Override
    public void visit(BLangConstRef node, T data) {
        analyzeNode(node, data);
        visit((BLangSimpleVarRef) node, data);
    }

    @Override
    public void visit(BLangDynamicArgExpr node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangElvisExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.lhsExpr, data);
        visitNode(node.rhsExpr, data);
    }

    @Override
    public void visit(BLangErrorConstructorExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.errorTypeRef, data);
        visitNode(node.errorDetail, data);
        visitNode(node.namedArgs, data);
    }

    @Override
    public void visit(BLangErrorVarRef node, T data) {
        analyzeNode(node, data);
        visitBLangVariableReference(node, data);
        visitNode(node.pkgAlias, data);
        visitNode(node.message, data);
        visitNode(node.cause, data);
        visitNode(node.detail, data);
        visitNode(node.restVar, data);
        visitNode(node.typeNode, data);
    }

    @Override
    public void visit(BLangFieldBasedAccess node, T data) {
        analyzeNode(node, data);
        visitBLangAccessExpression(node, data);
        visitNode(node.field, data);
    }

    @Override
    public void visit(BLangFieldBasedAccess.BLangStructFunctionVarRef node, T data) {
        analyzeNode(node, data);
        visit((BLangFieldBasedAccess) node, data);
    }

    public void visit(BLangFieldBasedAccess.BLangPrefixedFieldBasedAccess node, T data) {
        analyzeNode(node, data);
        visit((BLangFieldBasedAccess) node, data);
        visitNode(node.prefix, data);
    }

    @Override
    public void visit(BLangGroupExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.expression, data);
    }

    @Override
    public void visit(BLangIgnoreExpr node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangIndexBasedAccess node, T data) {
        analyzeNode(node, data);
        visitBLangIndexBasedAccess(node, data);
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangArrayAccessExpr node, T data) {
        analyzeNode(node, data);
        visitBLangIndexBasedAccess(node, data);
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangMapAccessExpr node, T data) {
        analyzeNode(node, data);
        visitBLangIndexBasedAccess(node, data);
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangJSONAccessExpr node, T data) {
        analyzeNode(node, data);
        visitBLangIndexBasedAccess(node, data);
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangTableAccessExpr node, T data) {
        analyzeNode(node, data);
        visitBLangIndexBasedAccess(node, data);
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangStringAccessExpr node, T data) {
        analyzeNode(node, data);
        visitBLangIndexBasedAccess(node, data);
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangXMLAccessExpr node, T data) {
        analyzeNode(node, data);
        visitBLangIndexBasedAccess(node, data);
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangStructFieldAccessExpr node, T data) {
        analyzeNode(node, data);
        visitBLangIndexBasedAccess(node, data);
    }

    @Override
    public void visit(BLangIndexBasedAccess.BLangTupleAccessExpr node, T data) {
        analyzeNode(node, data);
        visitBLangIndexBasedAccess(node, data);
    }

    @Override
    public void visit(BLangInferredTypedescDefaultNode node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangInvocation node, T data) {
        analyzeNode(node, data);
        visitNode(node.pkgAlias, data);
        visitNode(node.name, data);
        visitNode(node.expr, data);
        visitNode(node.argExprs, data);
        visitNode(node.requiredArgs, data);
        visitNode(node.restArgs, data);
    }

    @Override
    public void visit(BLangCollectContextInvocation node, T data) {
        analyzeNode(node, data);
        visitNode(node.invocation, data);
    }

    @Override
    public void visit(BLangInvocation.BFunctionPointerInvocation node, T data) {
        analyzeNode(node, data);
        visit((BLangInvocation) node, data);
    }

    @Override
    public void visit(BLangInvocation.BLangAttachedFunctionInvocation node, T data) {
        analyzeNode(node, data);
        visit((BLangInvocation) node, data);
    }

    @Override
    public void visit(BLangInvocation.BLangActionInvocation node, T data) {
        analyzeNode(node, data);
        visit((BLangInvocation) node, data);
    }

    @Override
    public void visit(BLangInvocation.BLangResourceAccessInvocation node, T data) {
        analyzeNode(node, data);
        visit((BLangInvocation) node, data);
    }

    @Override
    public void visit(BLangIsAssignableExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.lhsExpr, data);
        visitNode(node.typeNode, data);
    }

    @Override
    public void visit(BLangIsLikeExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
        visitNode(node.typeNode, data);
    }

    @Override
    public void visit(BLangLambdaFunction node, T data) {
        analyzeNode(node, data);
        visitNode(node.function, data);
    }

    @Override
    public void visit(BLangLetExpression node, T data) {
        analyzeNode(node, data);
        visitNode(node.letVarDeclarations, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangXMLIndexedStepExtend node, T data) {
        analyzeNode(node, data);
        visitNode(node.indexExpr, data);
    }

    public void visit(BLangXMLFilterStepExtend node, T data) {
        analyzeNode(node, data);
        visitNode(node.filters, data);
    }

    public void visit(BLangXMLMethodCallStepExtend node, T data) {
        analyzeNode(node, data);
        visitNode(node.invocation.name, data);
        visitNode(node.invocation.argExprs, data);
        visitNode(node.invocation.requiredArgs, data);
        visitNode(node.invocation.restArgs, data);
    }

    public void visit(BLangListConstructorExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.exprs, data);
    }

    @Override
    public void visit(BLangListConstructorExpr.BLangListConstructorSpreadOpExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangLiteral node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangMarkDownDeprecatedParametersDocumentation node, T data) {
        analyzeNode(node, data);
        visitNode(node.parameters, data);
    }

    @Override
    public void visit(BLangMarkDownDeprecationDocumentation node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangMarkdownDocumentationLine node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangMarkdownParameterDocumentation node, T data) {
        analyzeNode(node, data);
        visitNode(node.parameterName, data);
    }

    @Override
    public void visit(BLangMarkdownReturnParameterDocumentation node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangMatchGuard node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangNamedArgsExpression node, T data) {
        analyzeNode(node, data);
        visitNode(node.name, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangObjectConstructorExpression node, T data) {
        analyzeNode(node, data);
        visitNode(node.classNode, data);
        visitNode(node.typeInit, data);
        visitNode(node.referenceType, data);
    }

    @Override
    public void visit(BLangQueryAction node, T data) {
        analyzeNode(node, data);
        visitNode(node.queryClauseList, data);
        visitNode(node.doClause, data);
    }

    @Override
    public void visit(BLangQueryExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.queryClauseList, data);
        visitNode(node.fieldNameIdentifierList, data);
    }

    @Override
    public void visit(BLangRawTemplateLiteral node, T data) {
        analyzeNode(node, data);
        visitNode(node.strings, data);
        visitNode(node.insertions, data);
    }

    @Override
    public void visit(BLangRecordLiteral node, T data) {
        analyzeNode(node, data);
        visitNode(node.fields, data);
    }

    @Override
    public void visit(BLangRecordLiteral.BLangRecordKeyValueField node, T data) {
        analyzeNode(node, data);
        visitNode(node.key, data);
        visitNode(node.valueExpr, data);
    }

    @Override
    public void visit(BLangRecordLiteral.BLangRecordVarNameField node, T data) {
        analyzeNode(node, data);
        visit((BLangSimpleVarRef) node, data);
    }

    @Override
    public void visit(BLangRecordLiteral.BLangRecordSpreadOperatorField node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangRecordLiteral.BLangRecordKey node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangRecordLiteral.BLangStructLiteral node, T data) {
        analyzeNode(node, data);
        visit((BLangRecordLiteral) node, data);
    }

    @Override
    public void visit(BLangRecordLiteral.BLangMapLiteral node, T data) {
        analyzeNode(node, data);
        visit((BLangRecordLiteral) node, data);
    }

    @Override
    public void visit(BLangRecordVarRef node, T data) {
        analyzeNode(node, data);
        visitBLangVariableReference(node, data);
        visitNode(node.pkgAlias, data);
        visitNode(node.recordRefFields, data);
        visitNode(node.restParam, data);
    }

    @Override
    public void visit(BLangRecordVarRef.BLangRecordVarRefKeyValue node, T data) {
        analyzeNode(node, data);
        visitNode(node.variableName, data);
        visitNode(node.variableReference, data);
    }

    @Override
    public void visit(BLangRestArgsExpression node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangServiceConstructorExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.serviceNode, data);
    }

    @Override
    public void visit(BLangSimpleVarRef node, T data) {
        analyzeNode(node, data);
        visitBLangVariableReference(node, data);
        visitNode(node.pkgAlias, data);
        visitNode(node.variableName, data);
    }

    @Override
    public void visit(BLangSimpleVarRef.BLangLocalVarRef node, T data) {
        analyzeNode(node, data);
        visit((BLangSimpleVarRef) node, data);
    }

    @Override
    public void visit(BLangSimpleVarRef.BLangFieldVarRef node, T data) {
        analyzeNode(node, data);
        visit((BLangSimpleVarRef) node, data);
    }

    @Override
    public void visit(BLangSimpleVarRef.BLangPackageVarRef node, T data) {
        analyzeNode(node, data);
        visit((BLangSimpleVarRef) node, data);
    }

    @Override
    public void visit(BLangSimpleVarRef.BLangFunctionVarRef node, T data) {
        analyzeNode(node, data);
        visit((BLangSimpleVarRef) node, data);
    }

    @Override
    public void visit(BLangSimpleVarRef.BLangTypeLoad node, T data) {
        analyzeNode(node, data);
        visit((BLangSimpleVarRef) node, data);
    }

    @Override
    public void visit(BLangStatementExpression node, T data) {
        analyzeNode(node, data);
        visitNode(node.stmt, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangStringTemplateLiteral node, T data) {
        analyzeNode(node, data);
        visitNode(node.exprs, data);
    }

    @Override
    public void visit(BLangTableConstructorExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.tableKeySpecifier, data);
        visitNode(node.recordLiteralList, data);
    }

    @Override
    public void visit(BLangTernaryExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
        visitNode(node.thenExpr, data);
        visitNode(node.elseExpr, data);
    }

    @Override
    public void visit(BLangTransactionalExpr node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangTrapExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangTupleVarRef node, T data) {
        analyzeNode(node, data);
        visitBLangVariableReference(node, data);
        visitNode(node.expressions, data);
        visitNode(node.restParam, data);
    }

    @Override
    public void visit(BLangTypeConversionExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
        visitNode(node.typeNode, data);
        visitNode(node.annAttachments, data);
    }

    @Override
    public void visit(BLangTypedescExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.typeNode, data);
    }

    @Override
    public void visit(BLangTypeInit node, T data) {
        analyzeNode(node, data);
        visitNode(node.userDefinedType, data);
        visitNode(node.initInvocation, data);
        visitNode(node.argsExpr, data);
    }

    @Override
    public void visit(BLangTypeTestExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
        visitNode(node.typeNode, data);
    }

    @Override
    public void visit(BLangUnaryExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangWaitExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.exprList, data);
    }

    @Override
    public void visit(BLangWaitForAllExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.keyValuePairs, data);
    }

    @Override
    public void visit(BLangWaitForAllExpr.BLangWaitKeyValue node, T data) {
        analyzeNode(node, data);
        visitNode(node.key, data);
        visitNode(node.key, data);
        visitNode(node.valueExpr, data);
    }

    @Override
    public void visit(BLangWorkerFlushExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.workerIdentifier, data);
    }

    @Override
    public void visit(BLangAlternateWorkerReceive node, T data) {
        analyzeNode(node, data);
        visitNode(node.getWorkerReceives(), data);
    }

    @Override
    public void visit(BLangMultipleWorkerReceive node, T data) {
        analyzeNode(node, data);
        for (BLangMultipleWorkerReceive.BLangReceiveField rvField : node.getReceiveFields()) {
            visitNode(rvField.getKey(), data);
            visitNode(rvField.getWorkerReceive(), data);
        }
    }

    @Override
    public void visit(BLangWorkerReceive node, T data) {
        analyzeNode(node, data);
        visitNode(node.workerIdentifier, data);
    }

    @Override
    public void visit(BLangWorkerSyncSendExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.workerIdentifier, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangXMLAttribute node, T data) {
        analyzeNode(node, data);
        visitNode(node.name, data);
        visitNode(node.value, data);
    }

    @Override
    public void visit(BLangXMLCommentLiteral node, T data) {
        analyzeNode(node, data);
        visitNode(node.textFragments, data);
        visitNode(node.concatExpr, data);
    }

    @Override
    public void visit(BLangXMLElementAccess node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
        visitNode(node.filters, data);
    }

    @Override
    public void visit(BLangXMLElementFilter node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangXMLElementLiteral node, T data) {
        analyzeNode(node, data);
        visitNode(node.startTagName, data);
        visitNode(node.endTagName, data);
        visitNode(node.attributes, data);
        visitNode(node.children, data);
    }

    @Override
    public void visit(BLangXMLNavigationAccess node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
        visitNode(node.filters, data);
    }

    @Override
    public void visit(BLangExtendedXMLNavigationAccess node, T data) {
        analyzeNode(node, data);
        visitNode(node.stepExpr, data);
        visitNode(node.extensions, data);
    }

    public void visit(BLangXMLProcInsLiteral node, T data) {
        analyzeNode(node, data);
        visitNode(node.target, data);
        visitNode(node.dataFragments, data);
    }

    @Override
    public void visit(BLangXMLQName node, T data) {
        analyzeNode(node, data);
        visitNode(node.prefix, data);
        visitNode(node.localname, data);
    }

    @Override
    public void visit(BLangXMLQuotedString node, T data) {
        analyzeNode(node, data);
        visitNode(node.textFragments, data);
    }

    @Override
    public void visit(BLangXMLSequenceLiteral node, T data) {
        analyzeNode(node, data);
        visitNode(node.xmlItems, data);
    }

    @Override
    public void visit(BLangXMLTextLiteral node, T data) {
        analyzeNode(node, data);
        visitNode(node.textFragments, data);
    }

    // Match patterns

    @Override
    public void visit(BLangConstPattern node, T data) {
        analyzeNode(node, data);
        visitBLangMatchPattern(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangErrorCauseMatchPattern node, T data) {
        analyzeNode(node, data);
        visitBLangMatchPattern(node, data);
        visitNode(node.simpleMatchPattern, data);
        visitNode(node.errorMatchPattern, data);
    }

    @Override
    public void visit(BLangErrorFieldMatchPatterns node, T data) {
        analyzeNode(node, data);
        visitBLangMatchPattern(node, data);
        visitNode(node.namedArgMatchPatterns, data);
        visitNode(node.restMatchPattern, data);
    }

    @Override
    public void visit(BLangErrorMatchPattern node, T data) {
        analyzeNode(node, data);
        visitBLangMatchPattern(node, data);
        visitNode(node.errorMessageMatchPattern, data);
        visitNode(node.errorCauseMatchPattern, data);
        visitNode(node.errorFieldMatchPatterns, data);
        visitNode(node.errorTypeReference, data);
    }

    @Override
    public void visit(BLangErrorMessageMatchPattern node, T data) {
        analyzeNode(node, data);
        visitBLangMatchPattern(node, data);
        visitNode(node.simpleMatchPattern, data);
    }

    @Override
    public void visit(BLangFieldMatchPattern node, T data) {
        analyzeNode(node, data);
        visitBLangMatchPattern(node, data);
        visitNode(node.fieldName, data);
        visitNode(node.matchPattern, data);
        visitNode(node.matchPattern, data);
    }

    @Override
    public void visit(BLangListMatchPattern node, T data) {
        analyzeNode(node, data);
        visitBLangMatchPattern(node, data);
        visitNode(node.matchPatterns, data);
        visitNode(node.restMatchPattern, data);
    }

    @Override
    public void visit(BLangMappingMatchPattern node, T data) {
        analyzeNode(node, data);
        visitBLangMatchPattern(node, data);
        visitNode(node.fieldMatchPatterns, data);
        visitNode(node.restMatchPattern, data);
    }

    @Override
    public void visit(BLangNamedArgMatchPattern node, T data) {
        analyzeNode(node, data);
        visitBLangMatchPattern(node, data);
        visitNode(node.argName, data);
        visitNode(node.matchPattern, data);
    }

    @Override
    public void visit(BLangRestMatchPattern node, T data) {
        analyzeNode(node, data);
        visitBLangMatchPattern(node, data);
        visitNode(node.variableName, data);
    }

    @Override
    public void visit(BLangSimpleMatchPattern node, T data) {
        analyzeNode(node, data);
        visitBLangMatchPattern(node, data);
        visitNode(node.constPattern, data);
        visitNode(node.wildCardMatchPattern, data);
        visitNode(node.varVariableName, data);
    }

    @Override
    public void visit(BLangVarBindingPatternMatchPattern node, T data) {
        analyzeNode(node, data);
        visitBLangMatchPattern(node, data);
        visitNode(node.bindingPattern, data);
    }

    @Override
    public void visit(BLangWildCardMatchPattern node, T data) {
        analyzeNode(node, data);
        visitBLangMatchPattern(node, data);
    }

    // Statements

    @Override
    public void visit(BLangAssignment node, T data) {
        analyzeNode(node, data);
        visitNode(node.varRef, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangBlockStmt node, T data) {
        analyzeNode(node, data);
        visitNode(node.stmts, data);
    }

    @Override
    public void visit(BLangBreak node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangCompoundAssignment node, T data) {
        analyzeNode(node, data);
        visitNode(node.varRef, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangContinue node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangDo node, T data) {
        analyzeNode(node, data);
        visitNode(node.body, data);
        visitNode(node.onFailClause, data);
    }

    @Override
    public void visit(BLangErrorDestructure node, T data) {
        analyzeNode(node, data);
        visitNode(node.varRef, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangErrorVariableDef node, T data) {
        analyzeNode(node, data);
        visitNode(node.errorVariable, data);
    }

    @Override
    public void visit(BLangExpressionStmt node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangFail node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangForeach node, T data) {
        analyzeNode(node, data);
        visitNode((BLangNode) node.variableDefinitionNode, data);
        visitNode(node.body, data);
        visitNode(node.collection, data);
        visitNode(node.onFailClause, data);
    }

    @Override
    public void visit(BLangForkJoin node, T data) {
        analyzeNode(node, data);
        visitNode(node.workers, data);
    }

    @Override
    public void visit(BLangIf node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
        visitNode(node.body, data);
        visitNode(node.elseStmt, data);
    }

    @Override
    public void visit(BLangLock node, T data) {
        analyzeNode(node, data);
        visitNode(node.body, data);
        visitNode(node.onFailClause, data);
    }

    @Override
    public void visit(BLangLock.BLangLockStmt node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangLock.BLangUnLockStmt node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangMatchStatement node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
        visitNode(node.matchClauses, data);
        visitNode(node.onFailClause, data);
    }

    @Override
    public void visit(BLangPanic node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangRecordDestructure node, T data) {
        analyzeNode(node, data);
        visitNode(node.varRef, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangRecordVariableDef node, T data) {
        analyzeNode(node, data);
        visitNode(node.var, data);
    }

    @Override
    public void visit(BLangRetry node, T data) {
        analyzeNode(node, data);
        visitNode(node.retrySpec, data);
        visitNode(node.retryBody, data);
        visitNode(node.onFailClause, data);
    }

    @Override
    public void visit(BLangRetryTransaction node, T data) {
        analyzeNode(node, data);
        visitNode(node.retrySpec, data);
        visitNode(node.transaction, data);
    }

    @Override
    public void visit(BLangReturn node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangRollback node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangSimpleVariableDef node, T data) {
        analyzeNode(node, data);
        visitNode(node.var, data);
    }

    @Override
    public void visit(BLangTransaction node, T data) {
        analyzeNode(node, data);
        visitNode(node.transactionBody, data);
        visitNode(node.onFailClause, data);
    }

    @Override
    public void visit(BLangTupleDestructure node, T data) {
        analyzeNode(node, data);
        visitNode(node.varRef, data);
        visitNode(node.expr, data);
    }

    @Override
    public void visit(BLangTupleVariableDef node, T data) {
        analyzeNode(node, data);
        visitNode(node.var, data);
    }

    @Override
    public void visit(BLangWhile node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
        visitNode(node.body, data);
        visitNode(node.onFailClause, data);
    }

    @Override
    public void visit(BLangWorkerAsyncSendExpr node, T data) {
        analyzeNode(node, data);
        visitNode(node.expr, data);
        visitNode(node.workerIdentifier, data);
    }

    @Override
    public void visit(BLangXMLNSStatement node, T data) {
        analyzeNode(node, data);
        visitNode(node.xmlnsDecl, data);
    }

    @Override
    public void visit(BLangRegExpTemplateLiteral node, T data) {
        analyzeNode(node, data);
        visitNode(node.reDisjunction, data);
    }

    @Override
    public void visit(BLangReSequence node, T data) {
        analyzeNode(node, data);
        visitNode(node.termList, data);
    }

    @Override
    public void visit(BLangReAtomQuantifier node, T data) {
        analyzeNode(node, data);
        visitNode(node.atom, data);
        visitNode(node.quantifier, data);
    }

    @Override
    public void visit(BLangReAtomCharOrEscape node, T data) {
        analyzeNode(node, data);
        visitNode(node.charOrEscape, data);
    }

    @Override
    public void visit(BLangReQuantifier node, T data) {
        analyzeNode(node, data);
        visitNode(node.quantifier, data);
        visitNode(node.nonGreedyChar, data);
    }

    @Override
    public void visit(BLangReCharacterClass node, T data) {
        analyzeNode(node, data);
        visitNode(node.characterClassStart, data);
        visitNode(node.negation, data);
        visitNode(node.charSet, data);
        visitNode(node.characterClassEnd, data);
    }

    @Override
    public void visit(BLangReCharSet node, T data) {
        analyzeNode(node, data);
        visitNode(node.charSetAtoms, data);
    }

    @Override
    public void visit(BLangReCharSetRange node, T data) {
        analyzeNode(node, data);
        visitNode(node.lhsCharSetAtom, data);
        visitNode(node.dash, data);
        visitNode(node.rhsCharSetAtom, data);
    }

    @Override
    public void visit(BLangReAssertion node, T data) {
        analyzeNode(node, data);
        visitNode(node.assertion, data);
    }

    @Override
    public void visit(BLangReCapturingGroups node, T data) {
        analyzeNode(node, data);
        visitNode(node.openParen, data);
        visitNode(node.flagExpr, data);
        visitNode(node.disjunction, data);
        visitNode(node.closeParen, data);
    }

    @Override
    public void visit(BLangReDisjunction node, T data) {
        analyzeNode(node, data);
        visitNode(node.sequenceList, data);
    }

    @Override
    public void visit(BLangReFlagsOnOff node, T data) {
        analyzeNode(node, data);
        visitNode(node.flags, data);
    }

    @Override
    public void visit(BLangReFlagExpression node, T data) {
        analyzeNode(node, data);
        visitNode(node.questionMark, data);
        visitNode(node.questionMark, data);
        visitNode(node.questionMark, data);
    }

    // Types

    @Override
    public void visit(BLangArrayType node, T data) {
        analyzeNode(node, data);
        visitNode(node.elemtype, data);
        visitNode(node.sizes, data);
    }

    @Override
    public void visit(BLangBuiltInRefTypeNode node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangConstrainedType node, T data) {
        analyzeNode(node, data);
        visitNode(node.type, data);
        visitNode(node.constraint, data);
    }

    @Override
    public void visit(BLangErrorType node, T data) {
        analyzeNode(node, data);
        visitNode(node.detailType, data);
    }

    @Override
    public void visit(BLangFiniteTypeNode node, T data) {
        analyzeNode(node, data);
        visitNode(node.valueSpace, data);
    }

    @Override
    public void visit(BLangFunctionTypeNode node, T data) {
        analyzeNode(node, data);
        visitNode(node.params, data);
        visitNode(node.restParam, data);
        visitNode(node.returnTypeNode, data);
    }

    @Override
    public void visit(BLangIntersectionTypeNode node, T data) {
        analyzeNode(node, data);
        visitNode(node.constituentTypeNodes, data);
    }

    @Override
    public void visit(BLangLetVariable node, T data) {
        analyzeNode(node, data);
        visitNode((BLangNode) node.definitionNode, data);
    }

    @Override
    public void visit(BLangObjectTypeNode node, T data) {
        analyzeNode(node, data);
        visitNode(node.initFunction, data);
        visitBLangStructureTypeNode(node, data);
        visitNode(node.functions, data);
    }

    @Override
    public void visit(BLangRecordTypeNode node, T data) {
        analyzeNode(node, data);
        visitBLangStructureTypeNode(node, data);
        visitNode(node.restFieldType, data);
    }

    @Override
    public void visit(BLangStreamType node, T data) {
        analyzeNode(node, data);
        visitNode(node.type, data);
        visitNode(node.constraint, data);
        visitNode(node.error, data);
    }

    @Override
    public void visit(BLangTableTypeNode node, T data) {
        analyzeNode(node, data);
        visitNode(node.type, data);
        visitNode(node.constraint, data);
        visitNode(node.tableKeySpecifier, data);
        visitNode(node.tableKeyTypeConstraint, data);
    }

    @Override
    public void visit(BLangTupleTypeNode node, T data) {
        analyzeNode(node, data);
        visitNode(node.members, data);
        visitNode(node.restParamType, data);
    }

    @Override
    public void visit(BLangUnionTypeNode node, T data) {
        analyzeNode(node, data);
        visitNode(node.memberTypeNodes, data);
    }

    @Override
    public void visit(BLangUserDefinedType node, T data) {
        analyzeNode(node, data);
        visitNode(node.pkgAlias, data);
        visitNode(node.typeName, data);
    }

    @Override
    public void visit(BLangValueType node, T data) {
        analyzeNode(node, data);
    }

    @Override
    public void visit(BLangNaturalExpression node, T data) {
        analyzeNode(node, data);
        visitNode(node.arguments, data);
        visitNode(node.strings, data);
        visitNode(node.insertions, data);
    }

    // Private methods

    private void visitBLangVariableNode(BLangVariable node, T data) {
        visitNode(node.typeNode, data);
        visitNode(node.annAttachments, data);
        visitNode(node.markdownDocumentationAttachment, data);
        visitNode(node.expr, data);
    }

    private void visitBLangInvokableNode(BLangInvokableNode node, T data) {
        visitNode(node.name, data);
        visitNode(node.annAttachments, data);
        visitNode(node.markdownDocumentationAttachment, data);
        visitNode(node.requiredParams, data);
        visitNode(node.restParam, data);
        visitNode(node.returnTypeNode, data);
        visitNode(node.returnTypeAnnAttachments, data);
        visitNode(node.body, data);
        visitNode(node.defaultWorkerName, data);
    }

    private void visitBLangInputClause(BLangInputClause node, T data) {
        visitNode(node.collection, data);
        visitNode((BLangNode) node.variableDefinitionNode, data);
    }

    private void visitBLangAccessExpression(BLangAccessExpression node, T data) {
        visitNode(node.expr, data);
    }

    private void visitBLangIndexBasedAccess(BLangIndexBasedAccess node, T data) {
        visitBLangAccessExpression(node, data);
        visitNode(node.indexExpr, data);
    }

    private void visitBLangVariableReference(BLangVariableReference node, T data) {
    }

    private void visitBLangMatchPattern(BLangMatchPattern node, T data) {
        visitNode(node.matchExpr, data);
    }

    private void visitBLangStructureTypeNode(BLangStructureTypeNode node, T data) {
        visitNode(node.fields, data);
        visitNode(node.typeRefs, data);
    }
}
