
/***************************************************************************
 *   Copyright 2006-2018 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.net.tcp;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.Logger;
import net.usikkert.kouchat.util.Validate;

/**
 * Handles all the tcp connections.
 *
 * @author Christian Ihle
 */
public class TCPConnectionHandler implements TCPConnectionListener {

    private static final Logger LOG = Logger.getLogger(TCPConnectionHandler.class);

    private final Controller controller;
    private final Settings settings;
    private final ExecutorService executorService;
    private final Map<User, TCPUserClient> userClients;

    public TCPConnectionHandler(final Controller controller, final Settings settings) {
        Validate.notNull(controller, "Controller can not be null");
        Validate.notNull(settings, "Settings can not be null");

        this.controller = controller;
        this.settings = settings;
        this.executorService = Executors.newCachedThreadPool();
        this.userClients = new HashMap<>();
    }

    @Override
    public void socketAdded(final Socket socket) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                LOG.fine("Add socket start");

                final TCPClient client = new TCPClient(socket);
                final TCPUserIdentifier userIdentifier = new TCPUserIdentifier(controller, client);
                client.startListener();

                final User user = userIdentifier.waitForUser();

                if (user == null) {
                    LOG.fine("Add socket done. No user found.");
                    client.disconnect();
                    return;
                }

                addClient(user, client);

                LOG.fine("Add socket done. user=%s", user.getNick());
            }
        });
    }

    public void userAdded(final User user) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                LOG.fine("Add user start for user=%s", user.getNick());

                final TCPConnector tcpConnector = new TCPConnector(user);
                final Socket socket = tcpConnector.connect();

                if (socket == null) {
                    return;
                }

                final TCPClient client = new TCPClient(socket);
                client.startListener();

                addClient(user, client);
                client.send(String.valueOf(settings.getMe().getCode()));

                LOG.fine("Add user done for user=%s", user.getNick());
            }
        });
    }

    public void userRemoved(final User user) {
        final TCPUserClient userClient = userClients.remove(user);

        if (userClient != null) {
            userClient.disconnect();
        }
    }

    private void addClient(final User user, final TCPClient client) {
        final TCPUserClient userClient = userClients.get(user);

        if (userClient == null) {
            userClients.put(user, new TCPUserClient(client, user));
        } else {
            userClient.add(client);
        }
    }
}