package org.apache.maven.model.composition;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

/**
 * Handles the import of plugin management from other models into the target model.
 *
 * @author Andrés Almiray & Hervé Boutemy
 */
@Named
@Singleton
public class DefaultPluginManagementImporter
    implements PluginManagementImporter
{

    @Override
    public void importManagement( Model target, List<? extends PluginManagement> sources,
                                  ModelBuildingRequest request, ModelProblemCollector problems )
    {
        if ( sources != null && !sources.isEmpty() )
        {
            Map<String, Plugin> plugins = new LinkedHashMap<>();

            if ( target.getBuild() == null )
            {
                target.setBuild( new Build() );
            }
            
            PluginManagement pluginMgmt = target.getBuild().getPluginManagement();

            if ( pluginMgmt != null )
            {
                for ( Plugin plugin : pluginMgmt.getPlugins() )
                {
                    plugins.put( plugin.getKey(), plugin );
                }
            }
            else
            {
                pluginMgmt = new PluginManagement();
                target.getBuild().setPluginManagement( pluginMgmt );
            }

            for ( PluginManagement source : sources )
            {
                for ( Plugin plugin : source.getPlugins() )
                {
                    String key = plugin.getKey();
                    if ( !plugins.containsKey( key ) )
                    {
                        plugins.put( key, plugin );
                        // TODO: limit import from PluginManagement? from trust and flexibility points of view
                        // version, executions, dependencies, goals, configuration
                    }
                    // ignore plugin that already existed
                }
            }

            pluginMgmt.setPlugins( new ArrayList<>( plugins.values() ) );
        }
    }

}
