package com.redfox.voip_pro.de.timroes.axmlrpc.serializer;

import com.redfox.voip_pro.de.timroes.axmlrpc.XMLRPCException;
import com.redfox.voip_pro.de.timroes.axmlrpc.XMLUtil;
import com.redfox.voip_pro.de.timroes.axmlrpc.xmlcreator.XmlElement;
import org.w3c.dom.Element;

/**
 *
 * @author timroes
 */
public class IntSerializer implements Serializer {

	public Object deserialize(Element content) throws XMLRPCException {
		return Integer.parseInt(XMLUtil.getOnlyTextContent(content.getChildNodes()));
	}

	public XmlElement serialize(Object object) {
		return XMLUtil.makeXmlTag(SerializerHandler.TYPE_INT,
				object.toString());
	}

}
