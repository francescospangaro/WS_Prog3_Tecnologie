package Spesa;

import java.io.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;
import org.xml.sax.SAXException;

public class MyValidator {

    public static void validate(String XMLdocument, String XSDschema) throws SAXException, IOException {
        // creazione di uno schema XSD a partire dal file
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        File schemaFile = new File(XSDschema);
        Schema schema = factory.newSchema(schemaFile);
        // creazione di un validatore rispetto allo schema XSD
        Validator validator = schema.newValidator();
        // validazione del documento XML
        Source source = new StreamSource(XMLdocument);
        validator.validate(source);
    }

}
