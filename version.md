<!--

    Copyright (C) 2011-2012 Barchart, Inc. <http://www.barchart.com/>

    All rights reserved. Licensed under the OSI BSD License.

    http://www.opensource.org/licenses/bsd-license.php

-->

### 3.3.1
VarMarketDDF now applys Form-T trades, type DDF_Session.Market.EXT, to CURRENT_EXT bar.

Removed the unused PIT and NET types.

### 3.3.0
Changed constructor to require username and password, and removed those fields from login.  Changed FeedClientFactory accordingly.

FeedClientDDF now maintains an enum map of DDF_FeedEvents to EventPolicy objects.  The EventPolicy for an event is called when that event is posted internally.

There is a default relogin policy after for 3 seconds for any connection or login error event.

All login calls, either from user or as a result of an event policy are now handled by a LoginHandler.  

The LoginHandler maintains a thread which attempts to login on login() and loginWithDelay(mills) and posts the attempt result.  

It ignores all attempts to log in if the thread is active, preventing multiple logins.

Continued login attempts are disabled on a logout and reenabled on user login.


Changed the behavior of DDF_FeedHandler.  DDF_FeedHandler is now DDF_MessageListener.  It is only responsible for handling messages, not DDF_FeedEvents.

Added the bindStateListener to DDF_FeedHandler.  This allows user to bind a callback when the feed changes state.  States are Logged in, logged out, and
attempting to login.

### 3.2.3
Created DDF_NulVal and DDF_ClearVal classes so that the static null and clear instances of DDF values can be global to ddf-feed


### 3.2.2
Added VarBookDDF, VarBookTopDDF, and VarMarketDDF
Updated all refrences to barchart.feed.base V1.3.2

### 3.2.0
original release
