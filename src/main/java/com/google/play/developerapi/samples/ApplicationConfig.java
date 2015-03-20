/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.play.developerapi.samples;

import org.apache.commons.cli.*;

public class ApplicationConfig {
    public final String appName;
    public final String appId;
    public final String account;
    public final String secrets;
    public final String apkFilePath;

    public ApplicationConfig(CommandLine cmd) {
        appName = cmd.getOptionValue("appName");
        appId = cmd.getOptionValue("appId");
        account = cmd.getOptionValue("account");
        secrets = cmd.getOptionValue("secrets");
        apkFilePath = cmd.getOptionValue("apkFilePath");
    }

    public static ApplicationConfig parseArgs(String[] args) {
        Options options = new Options();

        try {
            CommandLineParser parser = new GnuParser();

            Option appId = new Option("appId", "Application id (package name)");
            appId.setArgs(1);
            appId.setRequired(true);
            options.addOption(appId);

            Option secrets = new Option("secrets", "Client secrets path");
            secrets.setArgs(1);
            secrets.setRequired(true);
            options.addOption(secrets);

            Option account = new Option("account", "Service account email");
            account.setArgs(1);
            account.setRequired(false);
            options.addOption(account);

            Option appName = new Option("appName", "Application name");
            appName.setArgs(1);
            appName.setRequired(false);
            options.addOption(appName);

            Option apkFilePath = new Option("apkFilePath", "Apk file path");
            apkFilePath.setArgs(1);
            apkFilePath.setRequired(false);
            options.addOption(apkFilePath);

            return new ApplicationConfig(parser.parse(options, args));
        } catch (ParseException ex) {
            System.err.println(ex.getMessage());

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("android-publisher-api", options);
            return null;
        }
    }
}
