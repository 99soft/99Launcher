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
import javax.inject.Singleton;

import org.nnsoft.nnlauncher.command.AbstractCommand;
import org.nnsoft.nnlauncher.command.ExecutionException;
import org.nnsoft.nnlauncher.command.FailureException;

@Singleton
public final class ApplicationPropertiesCommand
    extends AbstractCommand
{

    @Inject
    @Named( "name" )
    private String projectName;

    @Inject
    @Named( "version" )
    private String projectVersion;

    @Inject
    @Named( "build" )
    private String projectBuild;

    @Inject
    @Named( "java.version" )
    private String javaVersion;

    @Inject
    @Named( "java.vendor" )
    private String javaVendor;

    @Inject
    @Named( "java.home" )
    private String javaHome;

    @Inject
    @Named( "user.language" )
    private String userLanguage;

    @Inject
    @Named( "user.country" )
    private String userCountry;

    @Inject
    @Named( "sun.jnu.encoding" )
    private String encoding;

    @Inject
    @Named( "os.name" )
    private String osName;

    @Inject
    @Named( "os.version" )
    private String osVersion;

    @Inject
    @Named( "os.arch" )
    private String osArch;

    @Inject
    @Named( "path.separator" )
    private String pathSeparator;

    public void setJavaVersion( String javaVersion )
    {
        this.javaVersion = javaVersion;
    }

    public void setJavaVendor( String javaVendor )
    {
        this.javaVendor = javaVendor;
    }

    public void setJavaHome( String javaHome )
    {
        this.javaHome = javaHome;
    }

    public void setUserLanguage( String userLanguage )
    {
        this.userLanguage = userLanguage;
    }

    public void setUserCountry( String userCountry )
    {
        this.userCountry = userCountry;
    }

    public void setEncoding( String encoding )
    {
        this.encoding = encoding;
    }

    public void setOsName( String osName )
    {
        this.osName = osName;
    }

    public void setOsVersion( String osVersion )
    {
        this.osVersion = osVersion;
    }

    public void setOsArch( String osArch )
    {
        this.osArch = osArch;
    }

    public void setPathSeparator( String pathSeparator )
    {
        this.pathSeparator = pathSeparator;
    }

    public void execute()
        throws ExecutionException, FailureException
    {
        System.out.printf( "%s %s (%s)%n", projectName, projectVersion, projectBuild );
        System.out.printf( "Java version: %s, vendor: %s%n", javaVersion, javaVendor );
        System.out.printf( "Java home: %s%n", javaHome );
        System.out.printf( "Default locale: %s_%s, platform encoding: %s%n", userLanguage, userCountry, encoding );
        System.out.printf( "OS name: \"%s\", version: \"%s\", arch: \"%s\", family: \"%s\"%n",
                           osName, osVersion, osArch, getOsFamily() );
    }

    private final String getOsFamily()
    {
        String osName = this.osName.toLowerCase();
        String pathSep = pathSeparator;

        if ( osName.indexOf( "windows" ) != -1 )
        {
            return "windows";
        }
        else if ( osName.indexOf( "os/2" ) != -1 )
        {
            return "os/2";
        }
        else if ( osName.indexOf( "z/os" ) != -1 || osName.indexOf( "os/390" ) != -1 )
        {
            return "z/os";
        }
        else if ( osName.indexOf( "os/400" ) != -1 )
        {
            return "os/400";
        }
        else if ( pathSep.equals( ";" ) )
        {
            return "dos";
        }
        else if ( osName.indexOf( "mac" ) != -1 )
        {
            if ( osName.endsWith( "x" ) )
            {
                return "mac"; // MACOSX
            }
            return "unix";
        }
        else if ( osName.indexOf( "nonstop_kernel" ) != -1 )
        {
            return "tandem";
        }
        else if ( osName.indexOf( "openvms" ) != -1 )
        {
            return "openvms";
        }
        else if ( pathSep.equals( ":" ) )
        {
            return "unix";
        }

        return "undefined";
    }

}
