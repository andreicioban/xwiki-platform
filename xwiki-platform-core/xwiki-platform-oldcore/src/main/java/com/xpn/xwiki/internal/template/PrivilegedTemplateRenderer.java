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
package com.xpn.xwiki.internal.template;

import org.xwiki.component.annotation.Role;

/**
 * This component might elevate the privileges delegated to a template depending on some condition before evaluating the
 * template.
 * 
 * @version $Id$
 * @since 4.4.1
 */
@Role
public interface PrivilegedTemplateRenderer
{

    /**
     * Evaluate a template while potentially delegating programming rights to it.
     * 
     * @param content The template content.
     * @param templateName The template name.
     * @return The rendered result.
     */
    String evaluateTemplate(String content, String templateName);

}