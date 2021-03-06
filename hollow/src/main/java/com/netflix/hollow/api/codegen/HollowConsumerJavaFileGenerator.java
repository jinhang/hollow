/*
 *  Copyright 2017 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.hollow.api.codegen;

/**
 * Not intended for external consumption.
 * 
 * @see HollowAPIGenerator
 * 
 * @author dsu
 */
public abstract class HollowConsumerJavaFileGenerator implements HollowJavaFileGenerator {
    private final boolean usePackageGrouping;
    private final String packageName;
    private final String subPackageName;
    protected String className;

    public HollowConsumerJavaFileGenerator(String packageName, String subPackageName, boolean usePackageGrouping) {
        this.packageName = packageName;
        this.subPackageName = subPackageName;
        this.usePackageGrouping = usePackageGrouping;
    }

    public String getSubPackageName() {
        return subPackageName;
    }

    @Override
    public final String getClassName() {
        return className;
    }

    protected void appendPackageAndCommonImports(StringBuilder builder) {
        String fullPackageName = createFullPackageName(packageName, subPackageName, usePackageGrouping);
        if (!isEmpty(fullPackageName)) {
            builder.append("package ").append(fullPackageName).append(";\n\n");

            if (usePackageGrouping) {
                builder.append("import ").append(packageName).append(".*;\n");
                builder.append("import ").append(packageName).append(".core.*;\n");
                builder.append("import ").append(packageName).append(".collections.*;\n\n");
            }
        }
    }

    private String createFullPackageName(String packageName, String subPackageName, boolean usePackageGrouping) {
        if (usePackageGrouping && !isEmpty(packageName) && !isEmpty(subPackageName)) {
            return packageName + "."  + subPackageName;
        } else {
            return packageName;
        }

    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
