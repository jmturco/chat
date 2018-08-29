
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

import java.util.ArrayList;
import java.util.List;

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Logger;
import net.usikkert.kouchat.util.Validate;

/**
 * Maps one or more tcp clients to a user.
 *
 * @author Christian Ihle
 */
public class TCPUserClient implements TCPMessageListener {

    private static final Logger LOG = Logger.getLogger(TCPUserClient.class);

    private final List<TCPClient> clients;
    private final User user;

    public TCPUserClient(final TCPClient client, final User user) {
        Validate.notNull(client, "Client can not be null");
        Validate.notNull(user, "User can not be null");

        this.clients = new ArrayList<>();
        this.user = user;

        add(client);
    }

    public void add(final TCPClient client) {
        clients.add(client);
        client.registerListener(this);
    }

    public void disconnect() {
        for (final TCPClient client : clients) {
            client.disconnect();
        }

        clients.clear();
    }

    @Override
    public void messageArrived(final String message, final TCPClient client) {
        LOG.fine("Received message: %s", message);
    }
}