/*
 * Copyright (c) 2020 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.debugger.test.adapter.variables;

import org.apache.commons.lang3.tuple.Pair;
import org.ballerinalang.debugger.test.BaseTestCase;
import org.ballerinalang.debugger.test.utils.BallerinaTestDebugPoint;
import org.ballerinalang.debugger.test.utils.DebugTestRunner;
import org.ballerinalang.debugger.test.utils.DebugUtils;
import org.ballerinalang.test.context.BallerinaTestException;
import org.eclipse.lsp4j.debug.StoppedEventArguments;
import org.eclipse.lsp4j.debug.Variable;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.ballerinalang.debugger.test.utils.DebugTestRunner.VariableScope;

/**
 * Test class for variable visibility.
 */

@Test(enabled = false)
public class VariableVisibilityTest extends BaseTestCase {

    Pair<BallerinaTestDebugPoint, StoppedEventArguments> debugHitInfo;
    Map<String, Variable> globalVariables = new HashMap<>();
    Map<String, Variable> localVariables = new HashMap<>();
    DebugTestRunner debugTestRunner;

    @Override
    @BeforeClass
    public void setup() {
    }

    @Test(description = "Variable visibility test at the beginning(first line) of the main() method")
    public void initialVariableVisibilityTest() throws BallerinaTestException {
        String testProjectName = "variable-tests";
        String testModuleFileName = "main.bal";
        debugTestRunner = new DebugTestRunner(testProjectName, testModuleFileName, true);

        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 123));
        debugTestRunner.initDebugSession(DebugUtils.DebuggeeExecutionKind.RUN);
        debugHitInfo = debugTestRunner.waitForDebugHit(25000);
        globalVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), VariableScope.GLOBAL);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), VariableScope.LOCAL);

        // local variable visibility test at the beginning of the main() method (should be 0).
        Assert.assertEquals(localVariables.size(), 0);

        // global variable visibility test at the beginning of the main() method (should be 14).
        Assert.assertEquals(globalVariables.size(), 14);
    }

    @Test(description = "Variable visibility test in the middle of the main() method for a new variable",
            enabled = false)
    public void newVariableVisibilityTest() throws BallerinaTestException {
        String testProjectName = "variable-tests";
        String testModuleFileName = "main.bal";
        debugTestRunner = new DebugTestRunner(testProjectName, testModuleFileName, true);

        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 245));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 316));
        debugTestRunner.initDebugSession(DebugUtils.DebuggeeExecutionKind.RUN);
        debugHitInfo = debugTestRunner.waitForDebugHit(25000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);

        // local variable visibility test in the middle of the main() method,
        // all the variables above the debug point should be visible,
        Assert.assertEquals(localVariables.size(), 40);

        // debug point variable should not be visible
        Assert.assertFalse(localVariables.containsKey("byteVar"));

        // debug point variable should be visible when we go to the next line (STEP_OVER).
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.STEP_OVER);
        debugHitInfo = debugTestRunner.waitForDebugHit(15000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
        Assert.assertEquals(localVariables.size(), 41);
        Assert.assertTrue(localVariables.containsKey("byteVar"));

        // debug point variable should be visible when multiple function returned values are present.
        // test for https://github.com/ballerina-platform/ballerina-lang/issues/31385
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
        debugTestRunner.assertVariable(localVariables, "intVar", "128", "int");

        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.STEP_OVER);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
        debugTestRunner.assertVariable(localVariables, "intVar", "5", "int");
    }

    //Todo - Fix the test case after update 12 release
    @Test(description = "Variable visibility test in control flows", enabled = false)
    public void controlFlowVariableVisibilityTest() throws BallerinaTestException {
        String testProjectName = "variable-tests";
        String testModuleFileName = "main.bal";
        debugTestRunner = new DebugTestRunner(testProjectName, testModuleFileName, true);

        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 266));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 270));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 277));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 284));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 293));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 299));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 306));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 322));
        // Todo - enable after fixing https://github.com/ballerina-platform/ballerina-lang/issues/27738
        // debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 266));

        debugTestRunner.initDebugSession(DebugUtils.DebuggeeExecutionKind.RUN);
        debugHitInfo = debugTestRunner.waitForDebugHit(25000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);

        // local variable visibility test inside `let` expression
        Assert.assertEquals(localVariables.size(), 51);

        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);

        // local variable visibility test inside `if` statement.
        Assert.assertEquals(localVariables.size(), 49);

        // local variable visibility test inside `else` statement.
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
        Assert.assertEquals(localVariables.size(), 49);

        // local variable visibility test inside `else-if` statement.
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
        Assert.assertEquals(localVariables.size(), 49);

        // local variable visibility test inside `while` loop.
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
        Assert.assertEquals(localVariables.size(), 49);

        // local variable visibility test inside `foreach` loop.
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        // TODO - Enable test
        // Assert.assertEquals(localVariables.size(), 37);

        // local variable visibility test inside `match` statement.
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
        Assert.assertEquals(localVariables.size(), 50);

        // visibility test of local variable defined inside `if` statement
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
        Assert.assertEquals(localVariables.size(), 50);

        // Todo - enable after fixing https://github.com/ballerina-platform/ballerina-lang/issues/27738
        // local variable visibility test inside `foreach` statement + lambda function.
        // debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        // debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        // localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope
        // .LOCAL);
        // Assert.assertEquals(localVariables.size(), 38);
    }

    @Test(description = "Variable visibility test for global variables")
    public void globalVariableVisibilityTest() throws BallerinaTestException {
        String testProjectName = "variable-tests";
        String testModuleFileName = "main.bal";
        debugTestRunner = new DebugTestRunner(testProjectName, testModuleFileName, true);

        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 352));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 327));
        debugTestRunner.initDebugSession(DebugUtils.DebuggeeExecutionKind.RUN);
        debugHitInfo = debugTestRunner.waitForDebugHit(25000);
        globalVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), VariableScope.GLOBAL);

        // global variable visibility test outside main() method.
        Assert.assertEquals(globalVariables.size(), 14);

        // global variable visibility at the last line of the main() method.
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        globalVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), VariableScope.GLOBAL);
        Assert.assertEquals(globalVariables.size(), 14);

        // global constants
        debugTestRunner.assertVariable(globalVariables, "nameWithoutType", "\"Ballerina\"", "string");
        debugTestRunner.assertVariable(globalVariables, "nameWithType", "\"Ballerina\"", "string");
        // TODO: enable after #40896
//        debugTestRunner.assertVariable(globalVariables, "nameMap", "(debug_test_resources/variable_tests" +
//                ":0:$anonType$nameMap$_0 & readonly)", "record");
        debugTestRunner.assertVariable(globalVariables, "nilWithoutType", "()", "nil");
        debugTestRunner.assertVariable(globalVariables, "nilWithType", "()", "nil");
        debugTestRunner.assertVariable(globalVariables, "RED", "\"RED\"", "string");
        debugTestRunner.assertVariable(globalVariables, "BLUE", "\"Blue\"", "string");

        // global variables
        debugTestRunner.assertVariable(globalVariables, "stringValue", "\"Ballerina\"", "string");
        debugTestRunner.assertVariable(globalVariables, "decimalValue", "100.0", "decimal");
        debugTestRunner.assertVariable(globalVariables, "byteValue", "2", "int");
        debugTestRunner.assertVariable(globalVariables, "floatValue", "2.0", "float");
        debugTestRunner.assertVariable(globalVariables, "jsonValue", "json (size = 2)", "json");
        debugTestRunner.assertVariable(globalVariables, " /:@[`{~π_IL", "\"IL with global var\"", "string");
        debugTestRunner.assertVariable(globalVariables, "port", "9090", "int");
    }

    @Test(description = "Variable visibility test for local variables at the last line of main() method")
    public void localVariableVisibilityTest() throws BallerinaTestException {
        String testProjectName = "variable-tests";
        String testModuleFileName = "main.bal";
        debugTestRunner = new DebugTestRunner(testProjectName, testModuleFileName, true);

        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 327));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 360));
        debugTestRunner.initDebugSession(DebugUtils.DebuggeeExecutionKind.RUN);
        debugHitInfo = debugTestRunner.waitForDebugHit(25000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), VariableScope.LOCAL);
        globalVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), VariableScope.GLOBAL);

        // variable visibility test for closure
        // A closure is an inner anonymous function that has visibility to the scope of its enclosing environment.
        // It can access its own scope, its enclosing environment’s scope, and variables defined in the global scope.
        debugTestRunner.assertVariable(localVariables, "a", "3", "int");
        debugTestRunner.assertVariable(localVariables, "b", "3", "int");
        debugTestRunner.assertVariable(localVariables, "c", "34", "int");
        debugTestRunner.assertVariable(globalVariables, "port", "9090", "int");

        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), VariableScope.LOCAL);

        // var variable visibility test
        debugTestRunner.assertVariable(localVariables, "varVariable", "()", "nil");

        // boolean variable visibility test
        debugTestRunner.assertVariable(localVariables, "booleanVar", "true", "boolean");

        // int variable visibility test
        debugTestRunner.assertVariable(localVariables, "intVar", "14", "int");

        // float variable visibility test
        debugTestRunner.assertVariable(localVariables, "floatVar", "-10.0", "float");

        // decimal variable visibility test
        debugTestRunner.assertVariable(localVariables, "decimalVar", "3.5", "decimal");

        // string variable visibility test
        debugTestRunner.assertVariable(localVariables, "stringVar", "\"foo\"", "string");

        // xml variable visibility test
        debugTestRunner.assertVariable(localVariables, "xmlVar", "XMLElement", "xml");

        // array variable visibility test
        debugTestRunner.assertVariable(localVariables, "arrayVar", "any[4]", "array");

        // tuple variable visibility test
        debugTestRunner.assertVariable(localVariables, "tupleVar", "tuple[int,string] (size = 2)", "tuple");

        // map variable visibility test
        debugTestRunner.assertVariable(localVariables, "mapVar", "map (size = 4)", "map");

        // record variable visibility test
        debugTestRunner.assertVariable(localVariables, "recordVar", " /:@[`{~π_123_ƮέŞŢ_Student", "record");

        // readonly record variable visibility test
        debugTestRunner.assertVariable(localVariables, "readonlyRecordVar", "Pet", "record");

        // anonymous record variable visibility test
        debugTestRunner.assertVariable(localVariables, "anonRecord", "record {| string city; string country; |}",
                "record");

        // error variable visibility test
        debugTestRunner.assertVariable(localVariables, "errorVar", "SimpleErrorType", "error");

        // anonymous function variable visibility test
        debugTestRunner.assertVariable(localVariables, "anonFunctionVar",
                "isolated function (string,string) returns (string)", "function");

        // future variable visibility test
        debugTestRunner.assertVariable(localVariables, "futureVar", "future<int>", "future");

        // object variable visibility test (Person object)
        debugTestRunner.assertVariable(localVariables, "objectVar", "Person_\\ /<>:@[`{~π_ƮέŞŢ", "object");

        debugTestRunner.assertVariable(localVariables, "anonObjectVar", "Person_\\ /<>:@[`{~π_ƮέŞŢ", "object");

        // type descriptor variable visibility test
        debugTestRunner.assertVariable(localVariables, "typedescVar", "int", "typedesc");

        // union variable visibility test
        debugTestRunner.assertVariable(localVariables, "unionVar", "\"foo\"", "string");

        // optional variable visibility test
        debugTestRunner.assertVariable(localVariables, "optionalVar", "\"foo\"", "string");

        // any variable visibility test
        debugTestRunner.assertVariable(localVariables, "anyVar", "15.0", "float");

        // anydata variable visibility test
        debugTestRunner.assertVariable(localVariables, "anydataVar", "619", "int");

        // byte variable visibility test
        debugTestRunner.assertVariable(localVariables, "byteVar", "128", "int");

        // json variable visibility test
        debugTestRunner.assertVariable(localVariables, "jsonVar", "json (size = 3)", "json");

        // regexp variable visibility test
        debugTestRunner.assertVariable(localVariables, "regexVar", "re `[a-zA-Z0-9]`", "regexp");

        // table with key variable visibility test
        debugTestRunner.assertVariable(localVariables, "tableWithKeyVar", "table<Employee> (entries = 3)", "table");

        // table without key variable visibility test
        debugTestRunner.assertVariable(localVariables, "tableWithoutKeyVar", "table<Employee> (entries = 3)", "table");

        // stream variable visibility test
        debugTestRunner.assertVariable(localVariables, "oddNumberStream", "stream<int, error?>", "stream");

        // variables with quoted identifiers visibility test
        debugTestRunner.assertVariable(localVariables, " /:@[`{~π_var", "\"IL with special characters in var\"",
                "string");
        debugTestRunner.assertVariable(localVariables, "üňĩćőđę_var", "\"IL with unicode characters in var\"",
                "string");
        debugTestRunner.assertVariable(localVariables, "ĠĿŐΒȂɭ_ /:@[`{~π_json", "json (size = 0)", "json");

        // service variable visibility test
        debugTestRunner.assertVariable(localVariables, "serviceVar", "service", "service");

        // let expression visibility test
        debugTestRunner.assertVariable(localVariables, "letVar", "\"Hello Ballerina!\"", "string");
    }

    @Test(enabled = false, description = "Child variable visibility test for local variables at the last line of main" +
            "() method")
    public void localVariableChildrenVisibilityTest() throws BallerinaTestException {
        String testProjectName = "variable-tests";
        String testModuleFileName = "main.bal";
        debugTestRunner = new DebugTestRunner(testProjectName, testModuleFileName, true);

        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 327));
        debugTestRunner.initDebugSession(DebugUtils.DebuggeeExecutionKind.RUN);
        debugHitInfo = debugTestRunner.waitForDebugHit(25000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), VariableScope.LOCAL);

        // xml child variable visibility test
        Assert.assertNotNull(localVariables.get("xmlVar"));
        Map<String, Variable> xmlChildVariables = debugTestRunner.fetchChildVariables(localVariables.get("xmlVar"));
        debugTestRunner.assertVariable(xmlChildVariables, "attributes", "XMLAttributeMap (size = 16)", "map");
        debugTestRunner.assertVariable(xmlChildVariables, "children", "XMLSequence (size = 2)", "xml");

        // xml attributes child variable visibility test
        Map<String, Variable> xmlAttributesChildVariables =
                debugTestRunner.fetchChildVariables(xmlChildVariables.get("attributes"));
        debugTestRunner.assertVariable(xmlAttributesChildVariables, "gender", "\"male\"", "string");

        // xml children variable visibility test
        Map<String, Variable> xmlChildrenVariables =
                debugTestRunner.fetchChildVariables(xmlChildVariables.get("children"));
        debugTestRunner.assertVariable(xmlChildrenVariables, "[0]", "XMLElement", "xml");
        debugTestRunner.assertVariable(xmlChildrenVariables, "[1]", "XMLElement", "xml");

        // xml grand children variable visibility test
        Map<String, Variable> xmlGrandChildrenVariables =
                debugTestRunner.fetchChildVariables(xmlChildrenVariables.get("[0]"));
        debugTestRunner.assertVariable(xmlGrandChildrenVariables, "children", "XMLSequence (size = 1)", "xml");

        // array child variable visibility test
        Map<String, Variable> arrayChildVariables = debugTestRunner.fetchChildVariables(localVariables.get("arrayVar"));
        debugTestRunner.assertVariable(arrayChildVariables, "[0]", "1", "int");
        debugTestRunner.assertVariable(arrayChildVariables, "[1]", "20", "int");
        debugTestRunner.assertVariable(arrayChildVariables, "[2]", "-10.0", "float");
        debugTestRunner.assertVariable(arrayChildVariables, "[3]", "\"foo\"", "string");

        Map<String, Variable> byteChildVars = debugTestRunner.fetchChildVariables(localVariables.get("byteArrayVar"));
        debugTestRunner.assertVariable(byteChildVars, "[0]", "105", "byte");
        debugTestRunner.assertVariable(byteChildVars, "[1]", "166", "byte");

        // tuple child variable visibility test
        Map<String, Variable> tupleChildVariables = debugTestRunner.fetchChildVariables(localVariables.get("tupleVar"));
        debugTestRunner.assertVariable(tupleChildVariables, "[0]", "20", "int");
        debugTestRunner.assertVariable(tupleChildVariables, "[1]", "\"foo\"", "string");

        // map child variable visibility test
        Map<String, Variable> mapChildVariables = debugTestRunner.fetchChildVariables(localVariables.get("mapVar"));
        debugTestRunner.assertVariable(mapChildVariables, "city", "\"Colombo 03\"", "string");
        debugTestRunner.assertVariable(mapChildVariables, "country", "\"Sri Lanka\"", "string");
        debugTestRunner.assertVariable(mapChildVariables, "line1", "\"No. 20\"", "string");
        debugTestRunner.assertVariable(mapChildVariables, "line2", "\"Palm Grove\"", "string");

        // record child variable visibility test (Student record)
        Map<String, Variable> studentRecordChildVariables =
                debugTestRunner.fetchChildVariables(localVariables.get("recordVar"));
        debugTestRunner.assertVariable(studentRecordChildVariables, "1st_name", "\"John Doe\"", "string");
        debugTestRunner.assertVariable(studentRecordChildVariables, "grades", "Grades", "record");
        debugTestRunner.assertVariable(studentRecordChildVariables, "Ȧɢέ_ /:@[`{~π", "20", "int");
        debugTestRunner.assertVariable(studentRecordChildVariables, "course", "\"ballerina\"", "string");

        // record child variable visibility test (Grades record)
        Map<String, Variable> gradesChildVariables =
                debugTestRunner.fetchChildVariables(studentRecordChildVariables.get("grades"));
        debugTestRunner.assertVariable(gradesChildVariables, "chemistry", "65", "int");
        debugTestRunner.assertVariable(gradesChildVariables, "maths", "80", "int");
        debugTestRunner.assertVariable(gradesChildVariables, "physics", "75", "int");
        debugTestRunner.assertVariable(gradesChildVariables, "english", "80", "int");

        // anonymous record child variable visibility test
        Map<String, Variable> recordChildVariables =
                debugTestRunner.fetchChildVariables(localVariables.get("anonRecord"));
        debugTestRunner.assertVariable(recordChildVariables, "city", "\"London\"", "string");
        debugTestRunner.assertVariable(recordChildVariables, "country", "\"UK\"", "string");

        // error child variable visibility test
        Map<String, Variable> errorChildVariables = debugTestRunner.fetchChildVariables(localVariables.get("errorVar"));
        debugTestRunner.assertVariable(errorChildVariables, "details", "map (size = 1)", "map");
        debugTestRunner.assertVariable(errorChildVariables, "message", "\"SimpleErrorType\"", "string");

        // error details child variable visibility test
        Map<String, Variable> errorDetailsChildVariables =
                debugTestRunner.fetchChildVariables(errorChildVariables.get("details"));
        debugTestRunner.assertVariable(errorDetailsChildVariables, "message", "\"Simple error occurred\"", "string");

        // future child variable visibility test
        Map<String, Variable> futureChildVariables =
                debugTestRunner.fetchChildVariables(localVariables.get("futureVar"));
        debugTestRunner.assertVariable(futureChildVariables, "isDone", "true", "boolean");
        debugTestRunner.assertVariable(futureChildVariables, "result", "90", "int");

        // object child variable visibility test (Person object)
        Map<String, Variable> personObjectChildVariables =
                debugTestRunner.fetchChildVariables(localVariables.get("objectVar"));
        debugTestRunner.assertVariable(personObjectChildVariables, "1st_name", "\"John\"", "string");
        debugTestRunner.assertVariable(personObjectChildVariables, "address", "\"No 20, Palm grove\"", "string");
        debugTestRunner.assertVariable(personObjectChildVariables, "parent", "()", "nil");
        debugTestRunner.assertVariable(personObjectChildVariables, "email", "\"default@abc.com\"", "string");
        debugTestRunner.assertVariable(personObjectChildVariables, "Ȧɢέ_ /:@[`{~π", "0", "int");

        // anonymous object child variable visibility test (AnonPerson object)
        Map<String, Variable> anonObjectChildVariables =
                debugTestRunner.fetchChildVariables(localVariables.get("anonObjectVar"));
        debugTestRunner.assertVariable(anonObjectChildVariables, "1st_name", "\"John\"", "string");
        debugTestRunner.assertVariable(anonObjectChildVariables, "address", "\"No 20, Palm grove\"", "string");
        debugTestRunner.assertVariable(anonObjectChildVariables, "parent", "()", "nil");
        debugTestRunner.assertVariable(anonObjectChildVariables, "email", "\"default@abc.com\"", "string");
        debugTestRunner.assertVariable(anonObjectChildVariables, "Ȧɢέ_ /:@[`{~π", "0", "int");

        // TODO: Anonymous object's grand child variables are not visible. Need to fix it.
        // Variable[] anonPersonAddressChildVariables = getChildVariable(anonObjectChildVariables[0]);
        // Arrays.sort(anonPersonAddressChildVariables, compareByName);
        // debugTestRunner.assertVariable(anonPersonAddressChildVariables[0], "city", "Colombo", "string");
        // debugTestRunner.assertVariable(anonPersonAddressChildVariables[1], "country", "Sri Lanka", "string");

        // json child variable visibility test
        Map<String, Variable> jsonChildVariables = debugTestRunner.fetchChildVariables(localVariables.get("jsonVar"));
        debugTestRunner.assertVariable(jsonChildVariables, "color", "\"red\"", "string");
        debugTestRunner.assertVariable(jsonChildVariables, "name", "\"apple\"", "string");
        debugTestRunner.assertVariable(jsonChildVariables, "price", "40", "int");

        // table with key child variable visibility test
        Map<String, Variable> tableWithKeyChildVariables =
                debugTestRunner.fetchChildVariables(localVariables.get("tableWithKeyVar"));
        debugTestRunner.assertVariable(tableWithKeyChildVariables, "[0]", "Employee", "record");
        debugTestRunner.assertVariable(tableWithKeyChildVariables, "[1]", "Employee", "record");
        debugTestRunner.assertVariable(tableWithKeyChildVariables, "[2]", "Employee", "record");

        // table without key child variable visibility test
        Map<String, Variable> tableWithoutKeyChildVariables =
                debugTestRunner.fetchChildVariables(localVariables.get("tableWithoutKeyVar"));
        debugTestRunner.assertVariable(tableWithoutKeyChildVariables, "[0]", "Employee", "record");
        debugTestRunner.assertVariable(tableWithoutKeyChildVariables, "[1]", "Employee", "record");
        debugTestRunner.assertVariable(tableWithoutKeyChildVariables, "[2]", "Employee", "record");

        // service child variable visibility test
        Map<String, Variable> serviceChildVariables =
                debugTestRunner.fetchChildVariables(localVariables.get("serviceVar"));
        debugTestRunner.assertVariable(serviceChildVariables, "i", "5", "int");
    }

    @Test(description = "Object related variable visibility test")
    public void objectVariableVisibilityTest() throws BallerinaTestException {
        String testProjectName = "debug-instruction-tests-1";
        String testModuleFileName = "main.bal";
        debugTestRunner = new DebugTestRunner(testProjectName, testModuleFileName, true);

        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 21));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 25));
        debugTestRunner.initDebugSession(DebugUtils.DebuggeeExecutionKind.RUN);
        Pair<BallerinaTestDebugPoint, StoppedEventArguments> debugHitInfo = debugTestRunner.waitForDebugHit(25000);

        // local variable visibility test inside object init() method.
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
        Assert.assertEquals(localVariables.size(), 2);
        debugTestRunner.assertVariable(localVariables, "self", "Person", "object");
        debugTestRunner.assertVariable(localVariables, "name", "\"John\"", "string");

        // `self` child variable visibility test inside object init() method.
        Map<String, Variable> selfChildVariables = debugTestRunner.fetchChildVariables(localVariables.get("self"));
        debugTestRunner.assertVariable(selfChildVariables, "name", "()", "nil");

        // local variable visibility test inside object method.
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
        Assert.assertEquals(localVariables.size(), 1);
        debugTestRunner.assertVariable(localVariables, "self", "Person", "object");

        // `self` child variable visibility test inside object method.
        selfChildVariables = debugTestRunner.fetchChildVariables(localVariables.get("self"));
        debugTestRunner.assertVariable(selfChildVariables, "name", "\"John\"", "string");
    }

    // Need to be enabled after fixing https://github.com/ballerina-platform/ballerina-lang/issues/43636
    @Test(description = "Worker related variable visibility test", enabled = false)
    public void workerVariableVisibilityTest() throws BallerinaTestException {
        String testProjectName = "worker-tests";
        String testModuleFileName = "main.bal";
        debugTestRunner = new DebugTestRunner(testProjectName, testModuleFileName, true);

        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 23));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 31));
        debugTestRunner.initDebugSession(DebugUtils.DebuggeeExecutionKind.RUN);
        Pair<BallerinaTestDebugPoint, StoppedEventArguments> debugHitInfo = debugTestRunner.waitForDebugHit(25000);
        if (debugHitInfo.getKey().getLine() == 23) {
            // variable visibility test inside worker (only worker's variables should be visible)
            localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(),
                    DebugTestRunner.VariableScope.LOCAL);
            debugTestRunner.assertVariable(localVariables, "x", "10", "int");
            // variables outside worker should not be visible
            Assert.assertFalse(localVariables.containsKey("a"));
            // variable visibility test for workers outside fork (workers are visible outside fork() as futures).
            debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
            debugHitInfo = debugTestRunner.waitForDebugHit(10000);
            localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(),
                    DebugTestRunner.VariableScope.LOCAL);
            debugTestRunner.assertVariable(localVariables, "w1", "future<()>", "future");
            Assert.assertTrue(localVariables.containsKey("a"));
        } else {
            // variable visibility test for workers outside fork (workers are visible outside fork() as futures).
            localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(),
                    DebugTestRunner.VariableScope.LOCAL);
            debugTestRunner.assertVariable(localVariables, "w1", "future<()>", "future");
            Assert.assertTrue(localVariables.containsKey("a"));
            debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
            debugHitInfo = debugTestRunner.waitForDebugHit(10000);
            localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(),
                    DebugTestRunner.VariableScope.LOCAL);
            debugTestRunner.assertVariable(localVariables, "x", "10", "int");
            // variables outside worker should not be visible
            Assert.assertFalse(localVariables.containsKey("a"));
        }
    }

    @Test(description = "Binding pattern variables related visibility test")
    public void bindingPatternVariableVisibilityTest() throws BallerinaTestException {
        String testProjectName = "variable-tests-2";
        String testModuleFileName = "main.bal";
        debugTestRunner = new DebugTestRunner(testProjectName, testModuleFileName, true);

        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 35));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 40));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 43));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 46));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 49));
        debugTestRunner.addBreakPoint(new BallerinaTestDebugPoint(debugTestRunner.testEntryFilePath, 80));

        debugTestRunner.initDebugSession(DebugUtils.DebuggeeExecutionKind.RUN);
        Pair<BallerinaTestDebugPoint, StoppedEventArguments> debugHitInfo = debugTestRunner.waitForDebugHit(25000);

        // simple binding pattern variables
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
        debugTestRunner.assertVariable(localVariables, "profession", "\"Software Engineer\"", "string");

        // list binding pattern variables
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        // TODO: enable after fixing runtime issue https://github.com/ballerina-platform/ballerina-lang/issues/43623
//        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
//        debugTestRunner.assertVariable(localVariables, "id", "1234", "int");
//        debugTestRunner.assertVariable(localVariables, "firstName", "\"John Doe\"", "string");

        // mapping binding pattern variables
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        // TODO: enable after fixing runtime issue https://github.com/ballerina-platform/ballerina-lang/issues/43623
//        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
//        debugTestRunner.assertVariable(localVariables, "givenName", "\"Anne\"", "string");
//        debugTestRunner.assertVariable(localVariables, "surName", "\"Frank\"", "string");

        // error binding pattern variables
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        // TODO: enable after fixing runtime issue https://github.com/ballerina-platform/ballerina-lang/issues/43623
//        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
//        debugTestRunner.assertVariable(localVariables, "cause", "\"Database Error\"", "error");
//        debugTestRunner.assertVariable(localVariables, "code", "20", "int");
//        debugTestRunner.assertVariable(localVariables, "reason", "\"deadlock condition\"", "string");

        // list binding pattern inside foreach statement
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
        debugTestRunner.assertVariable(localVariables, "name", "\"John\"", "string");
        debugTestRunner.assertVariable(localVariables, "age", "30", "int");

        // list binding patterns inside match statement
        debugTestRunner.resumeProgram(debugHitInfo.getRight(), DebugTestRunner.DebugResumeKind.NEXT_BREAKPOINT);
        debugHitInfo = debugTestRunner.waitForDebugHit(10000);
        // TODO: enable after fixing runtime issue https://github.com/ballerina-platform/ballerina-lang/issues/43623
//        localVariables = debugTestRunner.fetchVariables(debugHitInfo.getRight(), DebugTestRunner.VariableScope.LOCAL);
//        debugTestRunner.assertVariable(localVariables, "remove", "Remove", "string");
//        debugTestRunner.assertVariable(localVariables, "all", "*", "string");
//        debugTestRunner.assertVariable(localVariables, "isDir", "true", "boolean");
    }

    @Override
    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        debugTestRunner.terminateDebugSession();
        globalVariables.clear();
        localVariables.clear();
    }
}
