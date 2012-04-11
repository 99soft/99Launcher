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

import static com.google.inject.Guice.createInjector;
import static com.google.inject.Scopes.SINGLETON;
import static java.lang.System.exit;
import static java.util.Collections.emptyMap;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;
import org.nnsoft.guice.sli4j.slf4j.Slf4jLoggingModule;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.inject.Injector;

@Singleton
public final class NNLauncher
{

    @Parameter( names = { "-h", "--help" }, description = "Display help information." )
    private boolean printHelp;

    @Parameter( names = { "-v", "--version" }, description = "Display version information." )
    private boolean showVersion;

    @Parameter( names = { "-X", "--verbose" }, description = "Produce execution debug output." )
    private boolean debug;

    @DynamicParameter(names = { "-D", "--define" }, description = "Define a system property" )
    private Map<String, String> applicationProperties = emptyMap();

    @Inject
    private Injector mainInjector;

    @Inject
    private JCommander jCommander;

    public void setMainInjector( Injector mainInjector )
    {
        this.mainInjector = mainInjector;
    }

    public void setjCommander( JCommander jCommander )
    {
        this.jCommander = jCommander;
    }

    public void execute( String...args )
    {
        jCommander.parse( args );

        if ( printHelp )
        {
            jCommander.usage();
            exit( -1 );
        }

        if ( showVersion )
        {
            try
            {
                mainInjector.getInstance( ApplicationPropertiesCommand.class ).execute();
            }
            catch ( Exception e )
            {
                // ignore, doesn't happen
            }
            exit( -1 );
        }

        // setup the logging stuff

        if ( debug )
        {
            System.setProperty( "logging.level", "DEBUG" );
        }
        else
        {
            System.setProperty( "logging.level", "INFO" );
        }

        // assume SLF4J is bound to logback in the current environment
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        try
        {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext( lc );
            // the context was probably already configured by default configuration
            // rules
            lc.reset();
            configurator.doConfigure( NNLauncher.class.getClassLoader().getResourceAsStream( "logback-config.xml" ) );
        }
        catch ( JoranException je )
        {
            // StatusPrinter should handle this
        }
    }

    public static void main( String[] args )
    {
        // setup the DI container
        createInjector( new Slf4jLoggingModule(),
        new ConfigurationModule()
        {

            @Override
            protected void bindConfigurations()
            {
                // java system properties
                bindSystemProperties();
                // OS environment properties
                bindEnvironmentVariables();
                // project properties
                bindProperties( getClass()
                                .getClassLoader()
                                .getResource( "META-INF/maven/org.99soft/99launcher/pom.properties" ) );
                // the JCommander
                bind( JCommander.class ).toProvider( JCommanderProvider.class ).in( SINGLETON );
            }

        } ).getInstance( NNLauncher.class ).execute( args );
    }

}
