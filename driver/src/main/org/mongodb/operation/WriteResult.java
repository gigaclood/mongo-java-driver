/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.operation;

public class WriteResult {
    private final MongoWrite write;
    private final CommandResult getLastErrorResult;

    public WriteResult(final MongoWrite write, final CommandResult getLastErrorResult) {
        this.write = write;
        this.getLastErrorResult = getLastErrorResult;
    }

    public MongoWrite getWrite() {
        return write;
    }


    public CommandResult getGetLastErrorResult() {
        return getLastErrorResult;
    }
}