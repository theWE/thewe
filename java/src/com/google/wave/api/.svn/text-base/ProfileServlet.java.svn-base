/* Copyright (c) 2009 Google Inc.
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

package com.google.wave.api;

import com.metaparadigm.jsonrpc.JSONSerializer;

import java.net.HttpURLConnection;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that handles profile related requests from Google Wave. This servlet
 * is registered at {@code /_wave/robot/profile}.
 * 
 * By default, the servlet responds with a JSON string that describes the Robot:
 * <pre>
 * {"profileUrl":"http://<server hostname>/_wave/robot/profile",
 *  "imageUrl":"http://<server hostname>/_wave/robot/profile.png",
 *  "name":"<server hostname>"}
 * </pre>
 * 
 * This servlet also provides a hook that subclasses can extend to resolve
 * profile that is in the robot domain, for example, Twitter user in the case of
 * a Twitter robot. This hook will be called if the request contains a "name"
 * query parameter. For example, the call to
 * {@code http://<server hostname/_wave/robot/profile?user=foo} returns:
 * <pre>
 * {"profileUrl":"http://twitter.com/foo",
 *  "imageUrl":"http://<the URL for the user's avatar>",
 *  "name":"<the display name of foo>"}
 * </pre>
 * 
 * @author mprasetya@google.com (Marcel Prasetya)
 */

public class ProfileServlet extends HttpServlet {

  /**
   * The MIME type of the response of this servlet.
   */
  private static final String JSON_MIME_TYPE = "application/json";
  
  /**
   * The query parameter to specify custom profile request.
   */
  private static final String NAME_QUERY_PARAMETER_KEY = "name";
  
  /**
   * The request object that was received.
   */
  private HttpServletRequest req;
  
  /**
   * The display name of the Robot.
   * @return The display name of the Robot.
   */
  public String getRobotName() {
    return req.getRemoteHost();
  }
  
  /**
   * The URL of the Robot Avatar image.
   * @return The URL of the Robot Avatar image.
   */
  public String getRobotAvatarUrl() {
    return "http://" + req.getRemoteHost() + "/_wave/robot/profile.png";
  }
  
  /**
   * The URL of the Robot Profile page.
   * @return The URL of the Robot Profile page.
   */
  public String getRobotProfilePageUrl() {
    return "http://" + req.getRemoteHost() + "/_wave/robot/profile";
  }

  /**
   * Get custom profile based on the "name" query parameter. If the robot
   * doesn't support custom profile, it can simply return null.
   *  
   * @return Custom profile based on "name" query parameter, or {@code null} if
   *     this robot doesn't support custom profile.
   */
  public ParticipantProfile getCustomProfile(String name) {
    return null;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    doPost(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    this.req = req;
    ParticipantProfile profile = null;
    
    // Try to get custom profile.
    String screenName = req.getParameter(NAME_QUERY_PARAMETER_KEY);    
    if (screenName != null) {
      profile = getCustomProfile(screenName);
    }
    
    // Set the default profile. 
    if (profile == null) {
      profile = new ParticipantProfile(getRobotName(), getRobotAvatarUrl(),
          getRobotProfilePageUrl());
    }

    try { 
      // Serialize profile into JSON.
      JSONSerializer serializer = new JSONSerializer();
      serializer.registerDefaultSerializers();
      String profileAsJson = serializer.toJSON(profile);
      
      // Write the result into the output stream.
      resp.setContentType(JSON_MIME_TYPE);
      resp.getWriter().write(profileAsJson);
      resp.setStatus(HttpURLConnection.HTTP_OK);
    } catch (Exception e) {
      resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
    }
  }
}
