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
import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.exit;
import static java.util.Collections.emptyMap;
import static org.slf4j.LoggerFactory.getILoggerFactory;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;
import org.nnsoft.guice.sli4j.slf4j.Slf4jLoggingModule;
import org.slf4j.Logger;

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
    @Named( "name" )
    private String projectName;

    @Inject
    @Named( "app.name" )
    private String applicationName;

    public void setMainInjector( Injector mainInjector )
    {
        this.mainInjector = mainInjector;
    }

    public void setProjectName( String projectName )
    {
        this.projectName = projectName;
    }

    public void setApplicationName( String applicationName )
    {
        this.applicationName = applicationName;
    }

    public int execute( String...args )
    {
        final JCommander jCommander = new JCommander( this );
        jCommander.setProgramName( applicationName );
        jCommander.parse( args );

        if ( printHelp )
        {
            jCommander.usage();
            return -1;
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
            return -1;
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
        final LoggerContext lc = (LoggerContext) getILoggerFactory();

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

        // RUN!

        final Logger logger = getLogger( getClass() );

        logger.info( "" );
        logger.info( "------------------------------------------------------------------------" );
        logger.info( projectName );
        logger.info( "------------------------------------------------------------------------" );
        logger.info( "" );

        int exit = 0;
        long start = currentTimeMillis();

        Throwable error = null;
        try
        {
            // TODO
        }
        catch ( Throwable t )
        {
            exit = 1;
            error = t;
        }
        finally
        {
            logger.info( "" );
            logger.info( "------------------------------------------------------------------------" );
            logger.info( "{} {}", projectName, ( exit < 0 ) ? "FAILURE" : "SUCCESS" );

            if ( exit > 0 )
            {
                logger.info( "" );

                if ( debug )
                {
                    logger.error( "Execution terminated with errors", error );
                }
                else
                {
                    logger.error( "Execution terminated with errors: {}", error.getMessage() );
                }

                logger.info( "" );
            }

            logger.info( "Total time: {}s", ( ( currentTimeMillis() - start ) / 1000 ) );
            logger.info( "Finished at: {}", new Date() );

            final Runtime runtime = getRuntime();
            final int megaUnit = 1024 * 1024;
            logger.info( "Final Memory: {}M/{}M",
                         ( runtime.totalMemory() - runtime.freeMemory() ) / megaUnit,
                         runtime.totalMemory() / megaUnit );

            logger.info( "------------------------------------------------------------------------" );
        }

        return exit;
    }

    public static void main( String[] args )
    {
        // setup the DI container
        exit( createInjector( new Slf4jLoggingModule(),
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
            }

        } ).getInstance( NNLauncher.class ).execute( args ) );
    }

}
