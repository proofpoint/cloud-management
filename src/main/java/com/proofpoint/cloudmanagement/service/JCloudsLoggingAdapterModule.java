/*
 * Copyright 2010 Proofpoint, Inc.
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
package com.proofpoint.cloudmanagement.service;

import org.jclouds.logging.BaseLogger;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.logging.config.LoggingModule;

public class JCloudsLoggingAdapterModule extends LoggingModule
{
    @Override
    public LoggerFactory createLoggerFactory()
    {
        return new LoggerFactory()
        {
            @Override
            public Logger getLogger(String category)
            {
                return new JCloudsAdaptedLogger(category);
            }
        };
    }

    public class JCloudsAdaptedLogger extends BaseLogger {

        private final com.proofpoint.log.Logger proofpointLogger;
        private final String category;

        public JCloudsAdaptedLogger(String category) {
            this.category = category;
            this.proofpointLogger = com.proofpoint.log.Logger.get(category);
        }

        @Override
        protected void logError(String message, Throwable e)
        {
            proofpointLogger.error(e, "%s", message);
        }

        @Override
        protected void logError(String message)
        {
            proofpointLogger.error("%s", message);
        }

        @Override
        protected void logWarn(String message, Throwable e)
        {
            proofpointLogger.warn(e, "%s", message);
        }

        @Override
        protected void logWarn(String message)
        {
            proofpointLogger.warn("%s", message);
        }

        @Override
        protected void logInfo(String message)
        {
            proofpointLogger.info("%s", message);
        }

        @Override
        protected void logDebug(String message)
        {
            proofpointLogger.debug("%s", message);
        }

        @Override
        protected void logTrace(String message)
        {
            proofpointLogger.debug("%s", message);
        }

        @Override
        public String getCategory()
        {
            return category;
        }

        @Override
        public boolean isTraceEnabled()
        {
            return proofpointLogger.isDebugEnabled();
        }

        @Override
        public boolean isDebugEnabled()
        {
            return proofpointLogger.isDebugEnabled();
        }

        @Override
        public boolean isInfoEnabled()
        {
            return proofpointLogger.isInfoEnabled();
        }

        @Override
        public boolean isWarnEnabled()
        {
            return true;
        }

        @Override
        public boolean isErrorEnabled()
        {
            return true;
        }
    }

}
