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

import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Insert;
import com.google.api.services.androidpublisher.model.Apk;
import com.google.api.services.androidpublisher.model.ApksListResponse;
import com.google.api.services.androidpublisher.model.AppEdit;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Lists all the apks for a given app.
 */
public class ListApks {

    private static final Log log = LogFactory.getLog(ListApks.class);

    public static void main(String[] args) throws Exception {
        ApplicationConfig cfg = ApplicationConfig.parseArgs(args);
        if (cfg == null) {
            return;
        }

        // Create the API service.
        final AndroidPublisher service = AndroidPublisherHelper.init(cfg.appName, cfg.account, cfg.secrets);
        final Edits edits = service.edits();

        // Create a new edit to make changes.
        Insert editRequest = edits
                .insert(cfg.appId, null /** no content */);
        AppEdit appEdit = editRequest.execute();

        // Get a list of apks.
        ApksListResponse apksResponse = edits
                .apks()
                .list(cfg.appId, appEdit.getId()).execute();

        // Print the apk info.
        for (Apk apk : apksResponse.getApks()) {
            System.out.println(
                    String.format("Version: %d - Binary sha1: %s", apk.getVersionCode(),
                            apk.getBinary().getSha1()));
        }
    }
}
