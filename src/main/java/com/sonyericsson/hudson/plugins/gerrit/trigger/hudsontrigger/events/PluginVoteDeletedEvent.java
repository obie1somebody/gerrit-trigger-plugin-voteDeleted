/*
 *  The MIT License
 *
 *  Copyright 2023 Hewlett Packard Enterprise. All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.events;

import hudson.Extension;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonyericsson.hudson.plugins.gerrit.trigger.Messages;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.GerritTriggeredEvent;
import com.sonymobile.tools.gerrit.gerritevents.dto.events.VoteDeleted;


/**
 * An event configuration that causes the build to be triggered when a vote is removed
 * by matching a RegEx.
 * @author Damien Keehn
 */
public class PluginVoteDeletedEvent extends PluginGerritEvent implements Serializable {
    /**
     * The descriptor for PluginVoteDeletedEvent.
     */
    @Extension
    @Symbol("voteDeletedContains")
    public static class PluginVoteDeletedEventDescriptor extends
            PluginGerritEventDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.VoteDeletedDisplayName();
        }
    }

    private static final long serialVersionUID = 1L;

    private String voteDeletedCommentContains;

    private static final Logger logger = LoggerFactory
            .getLogger(PluginVoteDeletedEvent.class);

    /**
     * Empty constructor for serializer.
     */
    public PluginVoteDeletedEvent() {
    }

    /**
     * Standard DataBoundConstructor.
     *
     * @param voteDeletedCommentContains a string containg a regular expression to match.
     */
    @DataBoundConstructor
    public PluginVoteDeletedEvent(String voteDeletedCommentContains) {
        this.voteDeletedCommentContains = voteDeletedCommentContains;
    }

    /**
     * Get the regular expression to match against comment.
     * @return a string containg a regex.
     */
    public String getVoteDeletedCommentContains() {
        return voteDeletedCommentContains;
    }

    /**
     * Gets related event class.
     * @return a Class
     */
    @Override
    public Class getCorrespondingEventClass() {
        return VoteDeleted.class;
    }

    /**
     * Check if the comment added matches the configured regular expression.
     * @param event a GerritTriggeredEvent.
     * @return true or false.
     */
    public boolean match(GerritTriggeredEvent event) {
        if (!super.shouldTriggerOn(event)) {
            return false;
        }
        Pattern p = Pattern
                .compile(voteDeletedCommentContains, Pattern.DOTALL | Pattern.MULTILINE);
        VoteDeleted ca = (VoteDeleted)event;
        logger.debug("input comment: '{}'", ca.getComment());
        return p.matcher(ca.getComment()).find();
    }
}
