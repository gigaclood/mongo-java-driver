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

package org.mongodb.connection.impl;

import org.mongodb.connection.ChangeEvent;
import org.mongodb.connection.ChangeListener;
import org.mongodb.connection.ClusterConnectionMode;
import org.mongodb.connection.ClusterDescription;
import org.mongodb.connection.ClusterSettings;
import org.mongodb.connection.ClusterableServer;
import org.mongodb.connection.ClusterableServerFactory;
import org.mongodb.connection.ServerAddress;
import org.mongodb.connection.ServerDescription;

import java.util.Arrays;

import static org.mongodb.assertions.Assertions.isTrue;
import static org.mongodb.connection.ClusterConnectionMode.Single;

/**
 * This class needs to be final because we are leaking a reference to "this" from the constructor
 */
final class SingleServerCluster extends BaseCluster {
    private final ClusterableServer server;

    public SingleServerCluster(final ClusterSettings settings, final ClusterableServerFactory serverFactory) {
        super(serverFactory);
        isTrue("one server in a direct cluster", settings.getHosts().size() == 1);
        isTrue("connection mode is single", settings.getMode() == ClusterConnectionMode.Single);
        // synchronized in the constructor because the change listener is re-entrant to this instance.
        // In other words, we are leaking a reference to "this" from the constructor.
        synchronized (this) {
            this.server = createServer(settings.getHosts().get(0), new ChangeListener<ServerDescription>() {
                @Override
                public void stateChanged(final ChangeEvent<ServerDescription> event) {
                    ClusterDescription oldDescription = getDescriptionNoWaiting();
                    updateDescription(event.getNewValue());
                    fireChangeEvent(new ChangeEvent<ClusterDescription>(oldDescription, getDescriptionNoWaiting()));
                }

            });
            updateDescription();
        }
    }

    private synchronized void updateDescription(final ServerDescription serverDescription) {
        updateDescription(new ClusterDescription(Arrays.asList(serverDescription), Single));
    }

    private void updateDescription() {
        updateDescription(server.getDescription());
    }

    @Override
    protected ClusterableServer getServer(final ServerAddress serverAddress) {
        isTrue("open", !isClosed());

        return server;
    }

    @Override
    public void close() {
        if (!isClosed()) {
            server.close();
            super.close();
        }
    }
}