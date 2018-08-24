
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

import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.settings.Settings;
import net.usikkert.kouchat.util.Validate;

/**
 * Network service for administration of tcp communication.
 *
 * @author Christian Ihle
 */
public class TCPNetworkService {

    private final TCPServer tcpServer;

    public TCPNetworkService(final Settings settings, final ErrorHandler errorHandler) {
        Validate.notNull(settings, "Settings can not be null");
        Validate.notNull(errorHandler, "Error handler can not be null");

        this.tcpServer = new TCPServer(settings, errorHandler);
    }

    public void startService() {
        tcpServer.startServer();
    }

    public void stopService() {
        tcpServer.stopServer();
    }
}
