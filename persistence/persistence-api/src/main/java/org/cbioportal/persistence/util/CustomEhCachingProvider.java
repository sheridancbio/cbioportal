/*
 * Copyright (c) 2019 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cbioportal.persistence.util;

import javax.cache.CacheManager;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.beans.factory.annotation.Value;
import java.net.URL;
import java.net.URI;
import java.io.File;

public class CustomEhCachingProvider extends EhcacheCachingProvider {

    @Value("${ehcache.xml.configuration}")
    private String xmlConfiguration;

    @Override
    public CacheManager getCacheManager() {

        CacheManager toReturn = null;
        File ehcache_config_file = new File("/data/sheridan/repos/sheridancbio/cbioportal/core/src/test/resources/ehcache-mixed.xml");
        URI ehcache_config_uri = ehcache_config_file.toURI();
//        System.out.println("\n\n\nATTEMPT TO CONSTRUCT PROVIDER using xml : " + xmlConfiguration);
//        System.out.flush();
        try {
//            toReturn = this.getCacheManager(getClass().getResource(xmlConfiguration).toURI(),
//                                            getClass().getClassLoader());
//            URL ehcache_config_url = getClass().getResource(xmlConfiguration);
//            System.out.println(" using URL : " + ehcache_config_url + "\n\n");
//            System.out.flush();

//            URI ehcache_config_uri = ehcache_config_url.toURI();
//            System.out.println(" using URI : " + ehcache_config_uri + "\n\n");
//            System.out.flush();

            toReturn = this.getCacheManager(ehcache_config_uri, getClass().getClassLoader());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return toReturn;
    }
}
 
