/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.wiki.test.ui;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.xwiki.test.ui.AbstractTest;
import org.xwiki.test.ui.SuperAdminAuthenticationRule;
import org.xwiki.test.ui.po.editor.WikiEditPage;
import org.xwiki.wiki.test.po.CreateWikiPage;
import org.xwiki.wiki.test.po.CreateWikiPageStepProvisioning;
import org.xwiki.wiki.test.po.CreateWikiPageStepUser;
import org.xwiki.wiki.test.po.DeleteWikiPage;
import org.xwiki.wiki.test.po.WikiHomePage;
import org.xwiki.wiki.test.po.WikiIndexPage;
import org.xwiki.wiki.test.po.WikiLink;

/**
 * UI tests for the wiki templates feature of the Wiki application.
 *
 * @version $Id$
 * @since 6.0M1
 */
public class WikiTemplateTest extends AbstractTest
{
    @Rule
    public SuperAdminAuthenticationRule superAdminAuthenticationRule =
            new SuperAdminAuthenticationRule(getUtil(), getDriver());

    private String TEMPLATE_CONTENT = "Content of the template";

    public void createTemplateWiki() throws Exception
    {
        WikiIndexPage wikiIndexPage = WikiIndexPage.gotoPage();
        CreateWikiPage createWikiPage = wikiIndexPage.createWiki();
        createWikiPage.setPrettyName("My new template");
        String wikiName = createWikiPage.getComputedName();
        assertEquals("mynewtemplate", wikiName);
        createWikiPage.setDescription("This is the template I do for the tests");
        createWikiPage.setIsTemplate(true);

        assertTrue(createWikiPage.isNextStepEnabled());

        CreateWikiPageStepUser createWikiPageStepUser = createWikiPage.goUserStep();
        WikiHomePage wikiHomePage = createWikiPageStepUser.createWithoutTemplate();

        // Modify the template content
        wikiHomePage.edit();
        WikiEditPage wikiEditPage = new WikiEditPage();
        wikiEditPage.setContent(TEMPLATE_CONTENT);
        wikiEditPage.clickSaveAndView();
        wikiEditPage.waitUntilPageIsLoaded();

        // Verify the template is in the list of templates in the wizard
        CreateWikiPage createWikiPage2 = wikiHomePage.createWiki();
        assertTrue(createWikiPage2.getTemplateList().contains("mynewtemplate"));

        // Verify the wiki is in the wiki index page.
        wikiIndexPage = WikiIndexPage.gotoPage().waitUntilPageIsLoaded();
        WikiLink wikiLink = wikiIndexPage.getWikiLink("My new template");
        if (wikiLink == null) {
            throw new Exception("The wiki [My new template] is not in the wiki index.");
        }
        assertTrue(wikiLink.getURL().endsWith("/xwiki/wiki/mynewtemplate/view/Main/"));

    }

    public void deleteTemplateWiki() throws Exception
    {
        // Go to the template wiki
        WikiIndexPage wikiIndexPage = WikiIndexPage.gotoPage().waitUntilPageIsLoaded();
        WikiLink templateWikiLink = wikiIndexPage.getWikiLink("My new template");
        if (templateWikiLink == null) {
            throw new Exception("The wiki [My new template] is not in the wiki index.");
        }
        WikiHomePage wikiHomePage = templateWikiLink.click();
        // Delete the wiki
        DeleteWikiPage deleteWikiPage = wikiHomePage.deleteWiki().confirm();
        assertTrue(deleteWikiPage.hasSuccessMessage());
        // Verify the wiki has been deleted
        wikiIndexPage = WikiIndexPage.gotoPage().waitUntilPageIsLoaded();
        assertNull(wikiIndexPage.getWikiLink("My new template"));
    }

    @Test
    public void createWikiFromTemplate() throws Exception
    {
        createTemplateWiki();

        WikiIndexPage wikiIndexPage = WikiIndexPage.gotoPage();
        CreateWikiPage createWikiPage = wikiIndexPage.createWiki();
        createWikiPage.setPrettyName("My new wiki");
        String wikiName = createWikiPage.getComputedName();
        assertEquals("mynewwiki", wikiName);
        createWikiPage.setTemplate("mynewtemplate");
        createWikiPage.setIsTemplate(false);
        createWikiPage.setDescription("My first wiki");
        CreateWikiPageStepUser createWikiPageStepUser = createWikiPage.goUserStep();
        CreateWikiPageStepProvisioning createWikiPageStepProvisioning = createWikiPageStepUser.createWithTemplate();
        assertEquals("The system is provisioning the wiki.", createWikiPageStepProvisioning.getStepTitle());
        // Finalize
        WikiHomePage wikiHomePage = createWikiPageStepProvisioning.finalizeCreation();

        // Verify the content is the same than in the template
        wikiHomePage.edit();
        WikiEditPage wikiEditPage = new WikiEditPage();
        assertEquals(wikiEditPage.getContent(), TEMPLATE_CONTENT);

        // Delete the wiki
        wikiEditPage.clickCancel();
        DeleteWikiPage deleteWikiPage = wikiHomePage.deleteWiki();
        deleteWikiPage = deleteWikiPage.confirm();
        assertTrue(deleteWikiPage.hasSuccessMessage());

        // Verify the wiki has been deleted
        wikiIndexPage = WikiIndexPage.gotoPage().waitUntilPageIsLoaded();
        assertNull(wikiIndexPage.getWikiLink("My new wiki"));

        // Delete the template wiki
        deleteTemplateWiki();
    }
}
