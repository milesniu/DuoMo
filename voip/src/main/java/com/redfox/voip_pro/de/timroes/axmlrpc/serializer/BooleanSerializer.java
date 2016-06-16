package com.redfox.voip_pro.de.timroes.axmlrpc.serializer;

import com.redfox.voip_pro.de.timroes.axmlrpc.XMLRPCException;
import com.redfox.voip_pro.de.timroes.axmlrpc.XMLUtil;
import com.redfox.voip_pro.de.timroes.axmlrpc.xmlcreator.XmlElement;
import org.w3c.dom.Element;

/**
 *
 * @author Tim Roes
 */
public class BooleanSerializer implements Serializer {

	public Object deserialize(Element content) throws XMLRPCException {
		return (XMLUtil.getOnlyTextContent(content.getChildNodes()).equals("1"))
				? Boolean.TRUE : Boolean.FALSE;
	}

	public XmlElement serialize(Object object) {
		return XMLUtil.makeXmlTag(SerializerHandler.TYPE_BOOLEAN,
				((Boolean)object == true) ? "1" : "0");
	}

}