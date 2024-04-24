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

package io.mantisrx.common.messages;

import io.mantisrx.shaded.com.fasterxml.jackson.databind.ObjectMapper;


public abstract class MantisMetaMessage {

    protected final ObjectMapper mapper = new ObjectMapper();

    public abstract String getType();

    public abstract long getTime();

    public abstract String getValue();
}
