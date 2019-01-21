package com.jhcomn.lambda.framework.lambda.base.dispatch.hdfs.selector.impl;

import com.jhcomn.lambda.framework.lambda.persistence.hdfs.callback.FSDataInputStreamCallback;
import com.jhcomn.lambda.mllib.base.IMLController;
import com.jhcomn.lambda.mllib.base.callback.PreprocessCallback;
import org.apache.hadoop.fs.FSDataInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by shimn on 2017/4/20.
 */
public class HelloWorldFSDataInputStreamCallbackImpl implements FSDataInputStreamCallback<Void> {

    private static final Logger log = LoggerFactory.getLogger(HelloWorldFSDataInputStreamCallbackImpl.class);

    private IMLController controller;
    private String tag;
    private String path;

    public HelloWorldFSDataInputStreamCallbackImpl(IMLController controller, String tag, String path) {
        this.controller = controller;
        this.tag = tag;
        this.path = path;
    }

    @Override
    public Void deserialize(FSDataInputStream fsis) {
        controller.preprocessWithStream(fsis, tag, path, new PreprocessCallback() {

            @Override
            public void onStart(String msg) {

            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onStop(String msg) {

            }
        });
        return null;
    }
}
