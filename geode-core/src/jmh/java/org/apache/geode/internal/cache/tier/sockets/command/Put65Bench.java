/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.tier.sockets.command;

import static org.apache.geode.SystemFailure.loadEmergencyClasses;
import static org.apache.geode.internal.cache.TXManagerImpl.NOTX;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.geode.SystemFailure;
import org.apache.geode.cache.Operation;
import org.apache.geode.internal.Version;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.TXManagerImpl;
import org.apache.geode.internal.cache.tier.Command;
import org.apache.geode.internal.cache.tier.sockets.CacheServerStats;
import org.apache.geode.internal.cache.tier.sockets.ClientProxyMembershipID;
import org.apache.geode.internal.cache.tier.sockets.Message;
import org.apache.geode.internal.cache.tier.sockets.Part;
import org.apache.geode.internal.cache.tier.sockets.ServerConnection;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

public class Put65Bench {

  @State(Scope.Benchmark)
  public static class ServerConnectionState {
    public Command command;
    public ServerConnection mockServerConnection;
    public Message message;

    @Setup(Level.Trial)
    public void setup() throws Exception {
      loadEmergencyClasses();

      this.command = Put65.getCommand();

      this.mockServerConnection = mock(ServerConnection.class);
      when(this.mockServerConnection.getClientVersion()).thenReturn(Version.CURRENT);

      TXManagerImpl txManager = mock(TXManagerImpl.class);
      GemFireCacheImpl cache = mock(GemFireCacheImpl.class);
      when(cache.getTxManager()).thenReturn(txManager);

      when(this.mockServerConnection.getCache()).thenReturn(cache);

      CacheServerStats cacheServerStats = mock(CacheServerStats.class);
      when(this.mockServerConnection.getCacheServerStats()).thenReturn(cacheServerStats);

      // .getDistributedMember()
      ClientProxyMembershipID mockProxyId = mock(ClientProxyMembershipID.class);
      when(this.mockServerConnection.getProxyID()).thenReturn(mockProxyId);

      Message errorResponseMessage = mock(Message.class);
      when(this.mockServerConnection.getErrorResponseMessage()).thenReturn(errorResponseMessage);

      Part regionNamePart = mock(Part.class);
      when(regionNamePart.getString()).thenReturn("regionNamePart");

      Part operationPart = mock(Part.class);
      when(operationPart.getObject()).thenReturn(Operation.UPDATE);

      Part flagsPart = mock(Part.class);
      when(flagsPart.getInt()).thenReturn(0);

      Part keyPart = mock(Part.class);
      when(keyPart.getObject()).thenReturn("keyPart");
      when(keyPart.getStringOrObject()).thenReturn("keyPart");

      Part isDeltaPart = mock(Part.class);
      when(isDeltaPart.getObject()).thenReturn(Boolean.FALSE);

      Part valuePart = mock(Part.class);
      when(valuePart.getObject()).thenReturn("valuePart");

      Part eventPart = mock(Part.class);
      when(eventPart.getObject()).thenReturn("eventPart");

      Part callbackArgPart = mock(Part.class);
      when(callbackArgPart.getObject()).thenReturn("callbackArgPart");

      message = mock(Message.class);

      when(message.getTransactionId()).thenReturn(NOTX);

      when(message.getPart(0)).thenReturn(regionNamePart);
      when(message.getPart(1)).thenReturn(operationPart);
      when(message.getPart(2)).thenReturn(flagsPart);
      when(message.getPart(3)).thenReturn(keyPart);
      when(message.getPart(4)).thenReturn(isDeltaPart);
      when(message.getPart(5)).thenReturn(valuePart);
      when(message.getPart(6)).thenReturn(eventPart);
      when(message.getPart(7)).thenReturn(callbackArgPart);
    }
  }

  // @Benchmark
  public void benchmark(ServerConnectionState state, Blackhole blackhole) {
    state.command.execute(state.message, state.mockServerConnection);
    // Message replyMessage = state.mockServerConnection.getReplyMessage();
    // blackhole.consume(replyMessage);
  }
}
