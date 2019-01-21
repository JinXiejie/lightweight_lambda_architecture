package com.jhcomn.lambda.framework.lambda.sync;

import com.jhcomn.lambda.framework.lambda.sync.base.ISyncController;
import com.jhcomn.lambda.framework.lambda.sync.model.ModelSyncController;
import com.jhcomn.lambda.packages.ml_model.SyncMLModel;

/**
 * Created by shimn on 2017/4/30.
 */
public class Test {

    public static void main(String[] args) {
        ISyncController controller = new ModelSyncController();
        SyncMLModel data = new SyncMLModel("1", "1", "TEST_JSON", "2017-05-01 12:35:00",
                "[\n" +
                        "  {\n" +
                        "    \"_id\": \"5906b4c2fcca6e3d82126f29\",\n" +
                        "    \"index\": 0,\n" +
                        "    \"guid\": \"44ad6c0a-ebcb-4599-9da2-c9c675e39230\",\n" +
                        "    \"isActive\": false,\n" +
                        "    \"balance\": \"$3,641.78\",\n" +
                        "    \"picture\": \"http://placehold.it/32x32\",\n" +
                        "    \"tags\": [\n" +
                        "      \"ad\",\n" +
                        "      \"eiusmod\",\n" +
                        "      \"nisi\",\n" +
                        "      \"pariatur\",\n" +
                        "      \"esse\",\n" +
                        "      \"pariatur\",\n" +
                        "      \"pariatur\"\n" +
                        "    ],\n" +
                        "    \"friends\": [\n" +
                        "      {\n" +
                        "        \"id\": 0,\n" +
                        "        \"name\": \"Owen Porter\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"id\": 1,\n" +
                        "        \"name\": \"Amparo Mack\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"id\": 2,\n" +
                        "        \"name\": \"Logan Barr\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"_id\": \"5906b4c227688404b331aad4\",\n" +
                        "    \"index\": 1,\n" +
                        "    \"guid\": \"e361d794-bd1e-4b0b-947b-2b489f161f01\",\n" +
                        "    \"isActive\": false,\n" +
                        "    \"balance\": \"$2,542.78\",\n" +
                        "    \"picture\": \"http://placehold.it/32x32\",\n" +
                        "    \"tags\": [\n" +
                        "      \"sit\",\n" +
                        "      \"ad\",\n" +
                        "      \"et\",\n" +
                        "      \"eu\",\n" +
                        "      \"sint\",\n" +
                        "      \"aute\",\n" +
                        "      \"eiusmod\"\n" +
                        "    ],\n" +
                        "    \"friends\": [\n" +
                        "      {\n" +
                        "        \"id\": 0,\n" +
                        "        \"name\": \"Kay Wells\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"id\": 1,\n" +
                        "        \"name\": \"Wooten Simpson\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"id\": 2,\n" +
                        "        \"name\": \"Ashlee Sawyer\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"_id\": \"5906b4c26770451a17df7518\",\n" +
                        "    \"index\": 2,\n" +
                        "    \"guid\": \"6dbaf116-bfba-44ee-b4ad-6dee9267941e\",\n" +
                        "    \"isActive\": false,\n" +
                        "    \"balance\": \"$2,570.17\",\n" +
                        "    \"picture\": \"http://placehold.it/32x32\",\n" +
                        "    \"tags\": [\n" +
                        "      \"id\",\n" +
                        "      \"anim\",\n" +
                        "      \"dolor\",\n" +
                        "      \"ex\",\n" +
                        "      \"consequat\",\n" +
                        "      \"excepteur\",\n" +
                        "      \"sint\"\n" +
                        "    ],\n" +
                        "    \"friends\": [\n" +
                        "      {\n" +
                        "        \"id\": 0,\n" +
                        "        \"name\": \"Ernestine Craft\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"id\": 1,\n" +
                        "        \"name\": \"Joyce Leonard\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"id\": 2,\n" +
                        "        \"name\": \"Bennett Henderson\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"_id\": \"5906b4c2380a4719c992774b\",\n" +
                        "    \"index\": 3,\n" +
                        "    \"guid\": \"f73f7a50-85e9-4fac-97ca-997501bf9056\",\n" +
                        "    \"isActive\": false,\n" +
                        "    \"balance\": \"$1,671.01\",\n" +
                        "    \"picture\": \"http://placehold.it/32x32\",\n" +
                        "    \"tags\": [\n" +
                        "      \"dolor\",\n" +
                        "      \"cillum\",\n" +
                        "      \"cillum\",\n" +
                        "      \"aute\",\n" +
                        "      \"anim\",\n" +
                        "      \"ad\",\n" +
                        "      \"officia\"\n" +
                        "    ],\n" +
                        "    \"friends\": [\n" +
                        "      {\n" +
                        "        \"id\": 0,\n" +
                        "        \"name\": \"Knowles Cleveland\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"id\": 1,\n" +
                        "        \"name\": \"Rosalyn Kane\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"id\": 2,\n" +
                        "        \"name\": \"Dolly Luna\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"_id\": \"5906b4c2a17bb63fb6b00666\",\n" +
                        "    \"index\": 4,\n" +
                        "    \"guid\": \"a298b470-0ce7-4be0-9c11-4f37e5357e3e\",\n" +
                        "    \"isActive\": false,\n" +
                        "    \"balance\": \"$1,703.58\",\n" +
                        "    \"picture\": \"http://placehold.it/32x32\",\n" +
                        "    \"tags\": [\n" +
                        "      \"officia\",\n" +
                        "      \"deserunt\",\n" +
                        "      \"ipsum\",\n" +
                        "      \"deserunt\",\n" +
                        "      \"ut\",\n" +
                        "      \"exercitation\",\n" +
                        "      \"officia\"\n" +
                        "    ],\n" +
                        "    \"friends\": [\n" +
                        "      {\n" +
                        "        \"id\": 0,\n" +
                        "        \"name\": \"Buchanan Gibson\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"id\": 1,\n" +
                        "        \"name\": \"Wanda Mullins\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"id\": 2,\n" +
                        "        \"name\": \"Alyssa Marsh\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }\n" +
                        "]");
//        controller.saveOrUpdate(data);
        SyncMLModel loadModel = (SyncMLModel) controller.load("1");
        if (loadModel != null)
            System.out.println(loadModel.toString());
//        controller.delete("4");
    }

}
