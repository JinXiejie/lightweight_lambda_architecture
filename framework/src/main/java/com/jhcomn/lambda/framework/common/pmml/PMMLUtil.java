//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jhcomn.lambda.framework.common.pmml;

import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import org.dmg.pmml.PMML;
import org.jpmml.model.ImportFilter;
import org.jpmml.model.JAXBUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PMMLUtil {
    private PMMLUtil() {
    }

    public static PMML unmarshal(InputStream is) throws SAXException, JAXBException {
        InputSource source = new InputSource(is);
        SAXSource transformedSource = ImportFilter.apply(source);
        return JAXBUtil.unmarshalPMML(transformedSource);
    }

    public static void marshal(PMML pmml, OutputStream os) throws JAXBException {
        StreamResult result = new StreamResult(os);
        JAXBUtil.marshalPMML(pmml, result);
    }
}
