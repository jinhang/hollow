/*
 *
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
 *
 */
package com.netflix.hollow.api.codegen.indexes;

import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.hollowImplClassname;
import static com.netflix.hollow.api.codegen.HollowCodeGenerationUtils.substituteInvalidChars;

import com.netflix.hollow.api.codegen.HollowAPIGenerator;
import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowOrdinalIterable;
import com.netflix.hollow.api.consumer.index.AbstractHollowHashIndex;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.core.HollowDataset;
import com.netflix.hollow.core.index.HollowHashIndexResult;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSchemaSorter;
import java.util.Collections;
import java.util.List;

/**
 * This class contains template logic for generating a {@link HollowAPI} implementation.  Not intended for external consumption.
 * 
 * @see HollowAPIGenerator
 * 
 */
public class HollowHashIndexGenerator extends HollowIndexGenerator {

    private final HollowDataset dataset;
    private final boolean isListenToDataRefreah;
    
    public HollowHashIndexGenerator(String packageName, String apiClassname, String classPostfix, boolean useAggressiveSubstitutions, HollowDataset dataset, boolean usePackageGrouping, boolean isListenToDataRefreah) {
        super(packageName, apiClassname, classPostfix, useAggressiveSubstitutions, usePackageGrouping);
        this.className = apiClassname + "HashIndex";
        this.dataset = dataset;
        this.isListenToDataRefreah = isListenToDataRefreah;
    }

    @Override
    public String generate() {
        List<HollowSchema> schemaList = HollowSchemaSorter.dependencyOrderedSchemaList(dataset);
        
        StringBuilder builder = new StringBuilder();
        appendPackageAndCommonImports(builder);
        
        builder.append("import " + HollowConsumer.class.getName() + ";\n");
        builder.append("import " + HollowHashIndexResult.class.getName() + ";\n");
        builder.append("import " + Collections.class.getName() + ";\n");
        builder.append("import " + Iterable.class.getName() + ";\n");
        builder.append("import " + AbstractHollowHashIndex.class.getName() + ";\n");
        builder.append("import " + AbstractHollowOrdinalIterable.class.getName() + ";\n\n");

        builder.append("public class " + className + " extends " + AbstractHollowHashIndex.class.getSimpleName() + "<" + apiClassname + "> {\n\n");

        builder.append("    public " + className + "(HollowConsumer consumer, String queryType, String selectFieldPath, String... matchFieldPaths) {\n");
        builder.append("        super(consumer, " + isListenToDataRefreah +", queryType, selectFieldPath, matchFieldPaths);\n");
        builder.append("    }\n\n");

        builder.append("    public " + className + "(HollowConsumer consumer, boolean isListenToDataRefreah, String queryType, String selectFieldPath, String... matchFieldPaths) {\n");
        builder.append("        super(consumer, isListenToDataRefreah, queryType, selectFieldPath, matchFieldPaths);\n");
        builder.append("    }\n\n");

        for(HollowSchema schema : schemaList) {
            builder.append("    public Iterable<" + hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions) + "> find" + substituteInvalidChars(schema.getName()) + "Matches(Object... keys) {\n");
            builder.append("        HollowHashIndexResult matches = idx.findMatches(keys);\n");
            builder.append("        if(matches == null) return Collections.emptySet();\n\n");
            builder.append("        return new AbstractHollowOrdinalIterable<" + hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions) + ">(matches.iterator()) {\n");
            builder.append("            public " + hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions) + " getData(int ordinal) {\n");
            builder.append("                return api.get" + hollowImplClassname(schema.getName(), classPostfix, useAggressiveSubstitutions) + "(ordinal);\n");
            builder.append("            }\n");
            builder.append("        };\n");
            builder.append("    }\n\n");
        }
        builder.append("}");

        return builder.toString();
    }

}
