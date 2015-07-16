/*
 * WebOSTVServiceTest
 * Connect SDK
 *
 * Copyright (c) 2015 LG Electronics.
 * Created by Oleksii Frolov on 16 Jul 2015
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
package com.connectsdk.service;

import com.connectsdk.core.MediaInfo;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.command.ServiceCommand;
import com.connectsdk.service.config.ServiceConfig;
import com.connectsdk.service.config.ServiceDescription;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RokuServiceTest {

    private StubRokuService service;
    private ServiceDescription serviceDescription;

    class StubRokuService extends RokuService {

        private Object response;
        private ServiceCommand<?> mLatestCommand;

        public StubRokuService(ServiceDescription serviceDescription, ServiceConfig serviceConfig) {
            super(serviceDescription, serviceConfig);
        }

        public void setResponse(Object response) {
            this.response = response;
        }

        @Override
        public void sendCommand(ServiceCommand<?> serviceCommand) {
            mLatestCommand = serviceCommand;
            serviceCommand.getResponseListener().onSuccess(response);
        }

    }

    @Before
    public void setUp() {
        serviceDescription = Mockito.mock(ServiceDescription.class);
        ServiceConfig serviceConfig = Mockito.mock(ServiceConfig.class);
        service = new StubRokuService(serviceDescription, serviceConfig);
    }

    @Test
    public void testServiceNotNull() {
        Assert.assertNotNull(service);
    }

    @Test
    public void testPlayVideo() {
        Mockito.when(serviceDescription.getIpAddress()).thenReturn("127.0.0.1");
        Mockito.when(serviceDescription.getPort()).thenReturn(13);
        MediaInfo mediaInfo = new MediaInfo("url", "video/mp4", "title", "description");
        MediaPlayer.LaunchListener listener = Mockito.mock(MediaPlayer.LaunchListener.class);
        MediaPlayer.MediaLaunchObject response = Mockito.mock(MediaPlayer.MediaLaunchObject.class);
        service.setResponse(response);
        service.playMedia(mediaInfo, false, listener);
        Assert.assertEquals("http://127.0.0.1:13/input/15985?t=v&u=url&k=(null)" +
                "&videoName=title&videoFormat=mp4", service.mLatestCommand.getTarget());
    }

    @Test
    public void testPlayAudio() {
        Mockito.when(serviceDescription.getIpAddress()).thenReturn("127.0.0.1");
        Mockito.when(serviceDescription.getPort()).thenReturn(13);
        MediaInfo mediaInfo = new MediaInfo("url", "audio/mp3", "title", "description");
        MediaPlayer.LaunchListener listener = Mockito.mock(MediaPlayer.LaunchListener.class);
        MediaPlayer.MediaLaunchObject response = Mockito.mock(MediaPlayer.MediaLaunchObject.class);
        service.setResponse(response);
        service.playMedia(mediaInfo, false, listener);
        Assert.assertEquals("http://127.0.0.1:13/input/15985?t=a&u=url&k=(null)" +
                "&songname=title&artistname=description&songformat=mp3&albumarturl=(null)",
                service.mLatestCommand.getTarget());
    }

    @Test
    public void testDisplayImage() {
        Mockito.when(serviceDescription.getIpAddress()).thenReturn("127.0.0.1");
        Mockito.when(serviceDescription.getPort()).thenReturn(13);
        MediaInfo mediaInfo = new MediaInfo("url", "image/jpeg", "title", "description");
        MediaPlayer.LaunchListener listener = Mockito.mock(MediaPlayer.LaunchListener.class);
        MediaPlayer.MediaLaunchObject response = Mockito.mock(MediaPlayer.MediaLaunchObject.class);
        service.setResponse(response);
        service.displayImage(mediaInfo, listener);
        Assert.assertEquals("http://127.0.0.1:13/input/15985?t=p&u=url&tr=crossfade",
                service.mLatestCommand.getTarget());
    }
}
