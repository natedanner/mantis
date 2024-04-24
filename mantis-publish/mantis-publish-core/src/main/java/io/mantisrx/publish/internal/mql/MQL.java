/*
 * Copyright 2019 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.mantisrx.publish.internal.mql;

import io.mantisrx.mql.jvm.core.Query;
import io.mantisrx.mql.shaded.clojure.java.api.Clojure;
import io.mantisrx.mql.shaded.clojure.lang.IFn;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MQL {

    private static final Logger LOG = LoggerFactory.getLogger(MQL.class);
    private static final IFn require = Clojure.var("io.mantisrx.mql.shaded.clojure.core", "require");
    private static final IFn cljMakeQuery = Clojure.var("io.mantisrx.mql.jvm.interfaces.server", "make-query");
    private static final IFn cljSuperset = Clojure.var("io.mantisrx.mql.jvm.interfaces.core", "queries->superset-projection");

    static {
        require.invoke(Clojure.read("io.mantisrx.mql.jvm.interfaces.server"));
        require.invoke(Clojure.read("io.mantisrx.mql.jvm.interfaces.core"));
    }

    public static void init() {
        LOG.info("Initializing MQL Runtime.");
    }

    public static Query query(String subscriptionId, String query) {
        return (Query) cljMakeQuery.invoke(subscriptionId, query.trim());
    }

    @SuppressWarnings("unchecked")
    public static Function<Map<String, Object>, Map<String, Object>> makeSupersetProjector(
            HashSet<Query> queries) {
        ArrayList<String> qs = new ArrayList<>(queries.size());
        for (Query query : queries) {
            qs.add(query.getRawQuery());
        }

        IFn ssProjector = (IFn) cljSuperset.invoke(new ArrayList(qs));
        return datum -> (Map<String, Object>) (ssProjector.invoke(datum));
    }

    public static String preprocess(String criterion) {
        return "true".equals(criterion.toLowerCase()) ? "select * where true" :
                "false".equals(criterion.toLowerCase()) ? "select * where false" :
                        criterion;
    }

    public static boolean isContradictionQuery(String query) {
        return "false".equals(query) ||
                "select * where false".equals(query) ||
                "select * from stream where false".equals(query);
    }
}
