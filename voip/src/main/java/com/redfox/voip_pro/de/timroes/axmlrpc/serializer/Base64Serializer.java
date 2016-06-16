package com.redfox.voip_pro.de.timroes.axmlrpc.serializer;

import com.redfox.voip_pro.de.timroes.axmlrpc.XMLRPCException;
import com.redfox.voip_pro.de.timroes.axmlrpc.XMLUtil;
import com.redfox.voip_pro.de.timroes.axmlrpc.xmlcreator.XmlElement;
import com.redfox.voip_pro.de.timroes.base64.Base64;
import org.w3c.dom.Element;

/**
 *
 * @author Tim Roes
 */
public class Base64Serializer implements Serializer {

	public Object deserialize(Element content) throws XMLRPCException {
		return Base64.decode(XMLUtil.getOnlyTextContent(content.getChildNodes()));
	}

	public XmlElement serialize(Object object) {
		return XMLUtil.makeXmlTag(SerializerHandler.TYPE_BASE64,
				Base64.encode((Byte[])object));
	}

}