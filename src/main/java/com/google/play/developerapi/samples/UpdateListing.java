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
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Commit;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Insert;
import com.google.api.services.androidpublisher.AndroidPublisher.Edits.Listings.Update;
import com.google.api.services.androidpublisher.model.AppEdit;
import com.google.api.services.androidpublisher.model.Listing;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Locale;

/**
 * Updates US and UK listings. Changes title, short-description, full-description and video for
 * en-US and en-GB locales.
 */
public class UpdateListing {

    private static final Log log = LogFactory.getLog(UpdateListing.class);

    private static final String US_LISTING_TITLE = "App Title US";
    private static final String US_LISTING_SHORT_DESCRITPION = "Bacon ipsum";
    private static final String US_LISTING_FULL_DESCRIPTION = "Dessert trunk truck";

    private static final String UK_LISTING_TITLE = "App Title UK";
    private static final String UK_LISTING_SHORT_DESCRITPION = "Pancetta ipsum";
    private static final String UK_LISTING_FULL_DESCRIPTION = "Pudding boot lorry";

    private static final String LISTINGS_PROMO_VIDEO =
            "https://www.youtube.com/watch?v=ZNSLQlNSPu8";

    public static void main(String[] args) throws Exception {
        ApplicationConfig cfg = ApplicationConfig.parseArgs(args);
        if (cfg == null) {
            return;
        }

        // Create the API service.
        AndroidPublisher service = AndroidPublisherHelper.init(cfg.appName, cfg.account, cfg.secrets);
        final Edits edits = service.edits();

        // Create an edit to update listing for application.
        Insert editRequest = edits
                .insert(cfg.appId, null /** no content */);
        AppEdit edit = editRequest.execute();
        final String editId = edit.getId();
        log.info(String.format("Created edit with id: %s", editId));

        // Update listing for US version of the application.
        final Listing newUsListing = new Listing();
        newUsListing.setTitle(US_LISTING_TITLE)
                .setFullDescription(US_LISTING_FULL_DESCRIPTION)
                .setShortDescription(US_LISTING_SHORT_DESCRITPION)
                .setVideo(LISTINGS_PROMO_VIDEO);

        Update updateUSListingsRequest = edits
                .listings()
                .update(cfg.appId, editId, Locale.US.toString(), newUsListing);
        Listing updatedUsListing = updateUSListingsRequest.execute();
        log.info(String.format("Created new US app listing with title: %s",
                updatedUsListing.getTitle()));

        // Create and update listing for UK version of the application.
        final Listing newUkListing = new Listing();
        newUkListing.setTitle(UK_LISTING_TITLE)
                .setFullDescription(UK_LISTING_FULL_DESCRIPTION)
                .setShortDescription(UK_LISTING_SHORT_DESCRITPION)
                .setVideo(LISTINGS_PROMO_VIDEO);

        Update updateUkListingsRequest = edits
                .listings()
                .update(cfg.appId, editId, Locale.UK.toString(), newUkListing);
        Listing updatedUkListing = updateUkListingsRequest.execute();
        log.info(String.format("Created new UK app listing with title: %s",
                updatedUkListing.getTitle()));

        // Commit changes for edit.
        Commit commitRequest = edits.commit(cfg.appId, editId);
        AppEdit appEdit = commitRequest.execute();
        log.info(String.format("App edit with id %s has been comitted", appEdit.getId()));
    }
}
