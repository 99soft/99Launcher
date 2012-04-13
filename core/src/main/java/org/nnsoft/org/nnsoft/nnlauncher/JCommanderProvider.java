package org.nnsoft.nnlauncher;

/*
 *    Copyright 2012 The 99 Software Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.beust.jcommander.JCommander;

public final class JCommanderProvider
    implements Provider<JCommander>
{

    @Inject
    private NNLauncher nnLauncher;

    @Inject
    @Named( "app.name" )
    private String applicationName;

    public void setNnLauncher( NNLauncher nnLauncher )
    {
        this.nnLauncher = nnLauncher;
    }

    public void setApplicationName( String applicationName )
    {
        this.applicationName = applicationName;
    }

    public JCommander get()
    {
        JCommander jCommander = new JCommander( nnLauncher );
        jCommander.setProgramName( applicationName );
        return jCommander;
    }

}