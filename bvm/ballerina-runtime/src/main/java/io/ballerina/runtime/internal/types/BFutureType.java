/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.runtime.internal.types;

import io.ballerina.runtime.api.Module;
import io.ballerina.runtime.api.constants.TypeConstants;
import io.ballerina.runtime.api.types.FutureType;
import io.ballerina.runtime.api.types.Type;
import io.ballerina.runtime.api.types.TypeTags;
import io.ballerina.runtime.api.types.semtype.BasicTypeBitSet;
import io.ballerina.runtime.api.types.semtype.Builder;
import io.ballerina.runtime.api.types.semtype.Context;
import io.ballerina.runtime.api.types.semtype.SemType;
import io.ballerina.runtime.internal.types.semtype.FutureUtils;

import java.util.Objects;
import java.util.Set;

/**
 * {@code BFutureType} represents a future value in Ballerina.
 *
 * @since 0.995.0
 */
public class BFutureType extends BType implements FutureType {

    private static final BasicTypeBitSet BASIC_TYPE = Builder.getFutureType();
    private static final SimpleTypeCheckFlyweightStore FLYWEIGHT_CACHE = new SimpleTypeCheckFlyweightStore();
    private final Type constraint;

    /**
     * Create a {@code {@link BFutureType}} which represents the future value.
     *
     * @param typeName string name of the type
     * @param pkg of the type
     */
    public BFutureType(String typeName, Module pkg) {
        super(typeName, pkg, Object.class, true);
        constraint = null;
    }

    public BFutureType(Type constraint) {
        super(TypeConstants.FUTURE_TNAME, null, Object.class, false);
        this.constraint = constraint;
        var flyweight = FLYWEIGHT_CACHE.get(constraint);
        this.typeCheckCache = flyweight.typeCheckCache();
        this.typeId = flyweight.typeId();
    }

    public Type getConstrainedType() {
        return constraint;
    }

    @Override
    public <V extends Object> V getZeroValue() {
        return null;
    }

    @Override
    public <V extends Object> V getEmptyValue() {
        return null;
    }

    @Override
    public int getTag() {
        return TypeTags.FUTURE_TAG;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof BFutureType other)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (constraint == other.constraint) {
            return true;
        }
        return Objects.equals(constraint, other.constraint);
    }

    @Override
    public BasicTypeBitSet getBasicType() {
        return BASIC_TYPE;
    }

    @Override
    public String toString() {
        return TypeConstants.FUTURE_TNAME + getConstraintString();
    }

    private String getConstraintString() {
        return constraint != null ? "<" + constraint + ">" : "";
    }

    @Override
    public SemType createSemType(Context cx) {
        if (constraint == null) {
            return Builder.getFutureType();
        }
        return FutureUtils.futureContaining(cx.env, tryInto(cx, constraint));
    }

    @Override
    protected boolean isDependentlyTypedInner(Set<MayBeDependentType> visited) {
        return constraint instanceof MayBeDependentType constraintType && constraintType.isDependentlyTyped(visited);
    }
}
