/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.util;

import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Pattern;


/**
 * SQL statement writer.
 *
 */
public class SQLStatementWriter {
    
    /** The root UUID. */
    final String rootUUID = "34c90c3068854cc0a85f11ad3c2b5708";
    
    /** The disciplines UUID.. */
    final String DisciplinesUUID = "34c90c3068854cc0a85f11ad3c2b5710";

    /** Enum for dropbox types. */
    enum DropboxType {USER, GROUP};
    
    /** Dropbox type. */
    DropboxType dropboxType = null;
    String inputTokensFile;

    /** 
     * Constructor with type and input file name.
     * @param type - the dropbox type.
     * @param f - input file name. 
     */
    public SQLStatementWriter(String type, String f) {
        inputTokensFile = f;
        dropboxType = type.equals(DropboxType.USER.name()) ? DropboxType.USER : DropboxType.GROUP;
    }

    /**
     * myclient mct --execute="select discipline_id from disciplines;" > /tmp/groupList.txt.
     * myclient mct --execute="select user_id, discipline_id from mct_users;" > /tmp/userList.txt.
     * @return map of strings
     */
    private Map<String, String> getTokens() {
        final Pattern whiteSpace = Pattern.compile("\\s+"); 

        Map<String,String> rv = new LinkedHashMap<String,String>();
        try {
            java.io.BufferedReader stdin = new java.io.BufferedReader(new FileReader(inputTokensFile));
            String line = null;
            while ((line = stdin.readLine()) != null) {
                if (dropboxType == DropboxType.USER) {
                    String[] ug = whiteSpace.split(line.trim());
                    rv.put(ug[0], ug[1]);
                } else{
                    rv.put(line.trim(), "");
                }
            }
        }
        catch (java.io.IOException e) { 
            e.printStackTrace();
        }
        return rv;
    }

    private void  substitute(Map<String, String> ug) {
        if (dropboxType == DropboxType.USER) {
            writeUserStatement(ug);
        } else {
            writeGroupStatements(ug);
        }
    }

    private void writeGroupStatements(Map<String, String> ug) {
        String stmt = null;

        for (String groupSub : ug.keySet()) {
            System.out.println("-- "+groupSub);
            String _disc_ =  nextComponentId(); 
            stmt = "set @rootDisciplineId = (SELECT component_id FROM component_spec where external_key = '/Disciplines');";
            System.out.println(stmt);

            stmt = "insert into component_spec (obj_version, component_name, external_key, component_type, shared, model_info, owner, component_id, creator_user_id, date_created) values (0, '_GROUPSUB_', null, 'gov.nasa.arc.mct.core.components.TelemetryDisciplineComponent', 1, null, 'admin', '_disc_','admin', NOW());";			
            stmt = stmt.replaceAll("_disc_", _disc_).replaceAll("_GROUPSUB_", groupSub);
            System.out.println(stmt);
            stmt = "set @parentMaxSeq = ifnull(((SELECT MAX(seq_no) FROM component_relationship where component_id = @rootDisciplineId)) , 0);";
            System.out.println(stmt);
            stmt = "insert into component_relationship (component_id, seq_no, associated_component_id) values (@rootDisciplineId, @parentMaxSeq + 1, '_disc_');";
            stmt = stmt.replaceAll("_disc_", _disc_);
            System.out.println(stmt);
            stmt = "set @lastObjVersion = (SELECT max(obj_version) FROM  component_spec where component_id=@rootDisciplineId);";
            System.out.println(stmt);
            stmt = "update component_spec set obj_version = (@lastObjVersion + 1) where component_id=@rootDisciplineId;";
            System.out.println(stmt);
            
            String _groupsDropBoxes_ =  nextComponentId(); 
            stmt = "insert into component_spec (obj_version, component_name, external_key, component_type, shared, model_info, owner, component_id, creator_user_id, date_created) values (0, '_GROUPSUB_\\'s Drop Boxes', null, 'gov.nasa.arc.mct.components.collection.CollectionComponent', 1, null, 'admin', '_groupsDropBoxes_','admin', NOW());";
            stmt = stmt.replaceAll("_disc_", _disc_).replaceAll("_groupsDropBoxes_", _groupsDropBoxes_).replaceAll("_GROUPSUB_", groupSub);
            System.out.println(stmt);
            stmt = "insert into component_relationship (component_id, seq_no, associated_component_id) values ('_disc_', 2, '_groupsDropBoxes_');";
            stmt = stmt.replaceAll("_disc_", _disc_).replaceAll("_groupsDropBoxes_", _groupsDropBoxes_);
            System.out.println(stmt);

            String _allGroupsDropBoxes_ =  nextComponentId(); 
            stmt = "insert into component_spec (obj_version, component_name, external_key, component_type, shared, model_info, owner, component_id, creator_user_id, date_created) values (0, 'All _GROUPSUB_\\'s Drop Boxes', null, 'gov.nasa.arc.mct.core.components.TelemetryAllDropBoxComponent', 1, null, '*', '_allGroupsDropBoxes_','admin', NOW());" ;
            stmt = stmt.replaceAll("_disc_", _disc_).replaceAll("_allGroupsDropBoxes_", _allGroupsDropBoxes_).replaceAll("_GROUPSUB_", groupSub);
            System.out.println(stmt);
            stmt = "insert into component_relationship (component_id, seq_no, associated_component_id) values ('_groupsDropBoxes_', 0, '_allGroupsDropBoxes_');" ;
            stmt = stmt.replaceAll("_disc_", _disc_).replaceAll("_groupsDropBoxes_", _groupsDropBoxes_).replaceAll("_allGroupsDropBoxes_", _allGroupsDropBoxes_).replaceAll("_GROUPSUB_", groupSub);
            System.out.println(stmt);
        }
    }


    private void writeUserStatement(Map<String, String> ug) {
        String stmt = null;

        for (Entry<String, String> ugEntry : ug.entrySet()) {

            System.out.println("-- "+ ugEntry.getKey()+ " in group "+ugEntry.getValue());

            String uuid1 =  nextComponentId(); //My Sandbox a child of root
            stmt = "set @rootComponentId = (SELECT component_id FROM component_spec where external_key = '/');";
            System.out.println(stmt);

            stmt = "insert into component_spec (component_name,  component_type, shared, model_info, owner, component_id, creator_user_id, date_created) values ('My Sandbox', 'gov.nasa.arc.mct.core.components.MineTaxonomyComponent', 0, null, 'xxUSERxx', 'uuid1', 'xxUSERxx', NOW());";
            stmt = stmt.replaceAll("xxUSERxx", ugEntry.getKey()).replaceAll("uuid1", uuid1);
            System.out.println(stmt);
            stmt = "set @parentMaxSeq = (SELECT MAX(seq_no) FROM component_relationship where component_id = @rootComponentId);";
            System.out.println(stmt);
            stmt = "insert  into component_relationship  (component_id, associated_component_id, seq_no) values (@rootComponentId, 'uuid1', @parentMaxSeq + 1);";
            stmt = stmt.replaceAll("uuid1", uuid1);
            System.out.println(stmt);
            stmt = "set @lastObjVersion = (SELECT max(obj_version) FROM  component_spec where component_id=@rootComponentId);";
            System.out.println(stmt);
            stmt = "update component_spec set obj_version = (@lastObjVersion + 1) where component_id=@rootComponentId;";
            System.out.println(stmt);

            String uuid2 =  nextComponentId(); //'_GROUPSUB_\'s Drop Box' , a child of My Sandbox and Group's Drop Boxes
            stmt = "insert into component_spec (component_name,  component_type, shared, model_info, owner, component_id, creator_user_id, date_created) values ('xxUSERxx\\'s Drop Box', 'gov.nasa.arc.mct.core.components.TelemetryUserDropBoxComponent', 1, null, '*', 'uuid2', 'xxUSERxx', NOW());";
            stmt = stmt.replaceAll("xxUSERxx", ugEntry.getKey()).replaceAll("uuid2", uuid2);
            System.out.println(stmt);
            stmt = "insert  into component_relationship  (component_id, associated_component_id, seq_no) values ('uuid1', 'uuid2', 0);";
            stmt = stmt.replaceAll("uuid1", uuid1).replaceAll("uuid2", uuid2);
            System.out.println(stmt);
            // Locate container for group drop boxes - "GROUP's Drop Boxes" - and add this user's drop box (uuid2s's Drop Box) as its child
            stmt = "set @parentID =  (SELECT component_id FROM  component_spec where component_name like '_GROUPSUB_%Drop Boxes');";
            stmt = stmt.replaceAll("_GROUPSUB_", ugEntry.getValue());
            System.out.println(stmt);
            stmt = "set @parentMaxSeq = (SELECT MAX(seq_no) FROM component_relationship where component_id = @parentID);";
            System.out.println(stmt);
            stmt = "insert into component_relationship (component_id, associated_component_id, seq_no) values  (@parentID, 'uuid2', @parentMaxSeq + 1);";
            stmt = stmt.replaceAll("uuid2", uuid2);
            System.out.println(stmt);
            // Locate drop box for all group members - "All GROUP's Drop Boxes" - and add this user's drop box (uuid2s's Drop Box) as its child
            stmt = "set @allParentID =  (SELECT component_id FROM  component_spec where component_name like 'All _GROUPSUB_%Drop Boxes');";
            stmt = stmt.replaceAll("_GROUPSUB_", ugEntry.getValue());
            System.out.println(stmt);
            stmt = "set @nextSeq = (SELECT MAX(seq_no) FROM component_relationship where component_id = @allParentID) + 1;";
            System.out.println(stmt);
            stmt = "insert into component_relationship (component_id, associated_component_id, seq_no) values  (@allParentID, 'uuid2', IFNULL(@nextSeq, 0) );";
            stmt = stmt.replaceAll("uuid2", uuid2);
            System.out.println(stmt);
        }
    }

/**
 * Generates sql code suitable for loading dropboxes.
 * 
 * For generic base:
java gov.nasa.arc.mct.util.SQLStatementWriter USER  ../../deployment/src/main/resources/persistence/base/userList.txt  > ../../deployment/src/main/resources/persistence/createDropboxesForUsers.sql
java gov.nasa.arc.mct.util.SQLStatementWriter GROUP ../../deployment/src/main/resources/persistence/base/groupList.txt  > ../../deployment/src/main/resources/persistence/createDropboxesForGroups.sql
 * 
 * For JSC site:
 java gov.nasa.arc.mct.util.SQLStatementWriter USER ../../deployment/src/main/resources/site/siteUserList.txt  > ../../deployment/src/main/resources/site/dropboxesForUsers.sql
 java gov.nasa.arc.mct.util.SQLStatementWriter GROUP ../../deployment/src/main/resources/site/siteGroupList.txt  > ../../deployment/src/main/resources/site/dropboxesForGroups.sql
 * 
 * for Orion
 java gov.nasa.arc.mct.util.SQLStatementWriter USER ../../deployment/src/main/resources/site/orion/siteUserList.txt  > ../../deployment/src/main/resources/site/orion/dropboxesForUsers.sql
 java gov.nasa.arc.mct.util.SQLStatementWriter GROUP ../../deployment/src/main/resources/site/orion/siteGroupList.txt  > ../../deployment/src/main/resources/site/orion/dropboxesForGroups.sql

 * @param args - main method array of arguments.
 */
    public static void main(String args[]) {
        Map<String, String> tokens = null;

        if (args.length != 2) {
            System.out.println("usage: [USER | GROUP] fqUserOrGroupFile ");
            return;
        }
        SQLStatementWriter statementWriter = new SQLStatementWriter(args[0], args[1]);
        tokens = statementWriter.getTokens();
        statementWriter.substitute(tokens);
    }


    /**
     * Gets the next randomly generated Java UUID by replacing all dashes with empty space.
     * @return UUID - randomly generated Java unique id.
     */
    public static String nextComponentId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
