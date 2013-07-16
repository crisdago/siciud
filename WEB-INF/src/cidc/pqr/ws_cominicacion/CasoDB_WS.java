package cidc.pqr.ws_cominicacion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import cidc.pqr.ws_cominicacion.LeerArchivoXML;


import cidc.general.ws_coneccion_Bizagi.ConeccionDB_WS;
import cidc.pqr.ws_Bizagi_obj.CasoDatos;
import cidc.pqr.ws_Bizagi_obj.PersonaDatos;
import cidc.pqr.xmlRespPersona.XmlRespCaso;
import cidc.pqr.ws_Bizagi_obj.Archivo64;
import  cidc.pqr.ws_Bizagi_obj.ParametrosDatos;
import cidc.pqr.xmlRespPersona.xmlRespParametros;

public class CasoDB_WS extends ConeccionDB_WS {

	CasoDatos caso = null;
	ParametrosDatos parametrosDatos = null;
	
	public CasoDatos getPersonaDatos(){
		return caso;
	}
	
	
	public void setCasoDatos(CasoDatos casodatos){
		this.caso= casodatos;
				
	}
	
	
public List<CasoDatos> consulta(CasoDatos datos)  {
	
	List<CasoDatos>infoCaso = new ArrayList<CasoDatos>();
	
	caso = datos;
	return infoCaso;
}


public CasoDatos  CrearCaso (CasoDatos datosForm, PersonaDatos persona) throws IOException{
	
	super.setConnectionWF();
	String doc64 ="";
	String nombreArchivo ="";
	String cadena="";
	if(datosForm.getArchivoCaso()!=null){
		Archivo64 convertir64 = new Archivo64();
		//String ruta = DatosForm.getArchivoCaso();
		doc64 = convertir64.encodeFileToBase64Binary(datosForm.getArchivoCaso());
		nombreArchivo=datosForm.getArchivoCaso().getName();
		cadena="<ArchivosdelCaso><File fileName=\""+nombreArchivo+"\">"+doc64+"</File></ArchivosdelCaso>";
	}
	
	String xmlCrearCaso= "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soa=\"http://SOA.BizAgi/\">"
    		+"<soapenv:Header/>"
	        +"<soapenv:Body>"
	+"<soa:createCasesAsString>"
	        +"<!--Optional:-->"+"<arg0><![CDATA[<BizAgiWSParam><domain>UD</domain><userName>Recepcion</userName><Cases><Case><Process>AtencionDeAccionesCiudadan</Process><Entities><SolicituddeAccionesCiuda>" +
	        		"<TipodeSolicitante businessKey=\"id="+datosForm.getTipodeSolicitante()+"\"/>" +
	        		"<MediodeRecepcion businessKey=\"id="+datosForm.getMedioDeRecepcion()+"\"/>"+
	        		"<TipodeRequerimiento businessKey=\"id="+datosForm.getTipoDeRequerimiento()+"\"/>"+
	        		"<Asunto>"+datosForm.getAsunto()+"</Asunto>" +
	        		cadena+
	        		"<Descripcion>"+datosForm.getDescripcion()+"</Descripcion>"+
	        		"<FlagsdelCaso><EscaladodeOtraDependencia>false</EscaladodeOtraDependencia><RecibirNotificacionesporCo>"+datosForm.getRecibirNotificacionesCorreo()+"</RecibirNotificacionesporCo></FlagsdelCaso>"+
	        		"<ProyectodeInvestigacion><ProyectodeInvestigacion>"+persona.getProyInv()+"</ProyectodeInvestigacion><Codigo>"+persona.getCodigo()+"</Codigo><Facultad>"+persona.getFacultad()+"</Facultad></ProyectodeInvestigacion>"+
	        		"<Persona businessKey=\"id="+persona.getPersonaID()+"\"/>"+
	        		//"<Persona businesskey=\"id="+persona.getPersonaID()+"\"/>"+
	        	//	 "<TipodeSolicitanteInterno businessKey=\"id="+persona.getTipoInterno()+"\"/>" +//---------------------------------------------------
               //      "<ProyectodeInvestigacion>" +
                  //           "<ProyectodeInvestigacion>"+persona.getProyInv()+"</ProyectodeInvestigacion><Codigo>"+persona.getCodigo()+"</Codigo><Facultad>"+persona.getFaculta()+"</Facultad></ProyectodeInvestigacion>"+
	        		
	        				"</SolicituddeAccionesCiuda>" +
	        			
	        					
	        		"</Entities></Case></Cases></BizAgiWSParam>]]></arg0>"
	        +"</soa:createCasesAsString>"
	+"</soapenv:Body>"
	+"</soapenv:Envelope>";

			String crearCaso = super.httpostConsultaEM(xmlCrearCaso);
			XmlRespCaso casoCreado = new XmlRespCaso();
			System.out.println("xml: "+xmlCrearCaso);
			try {
				System.out.println(crearCaso);
				caso = casoCreado.CrearCaso(crearCaso, datosForm);
				
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	return caso;

}

public ParametrosDatos consultarCasoPQR (String numeroCaso){
	
	super.setConnectionEM();
	LeerArchivoXML leerArchivoXml = new LeerArchivoXML();
	String xmlFileName = "D:/TOMCAT 6.0/webapps/siciud/WEB-INF/src/cidc/pqr/archivosXml/consultaCasoPQR.xml";
    Document document = leerArchivoXml.getDocument( xmlFileName );
    List listaElementos = document.selectNodes("/soapenv:Envelope/soapenv:Body/soa:getCaseDataUsingSchemaAsString/arg0");
    Iterator iteraElementos = listaElementos.iterator();
	
	while(iteraElementos.hasNext()){
		Element e = (Element)iteraElementos.next();

		e.setText(numeroCaso);

	}

    String xmlConsultarCasoPQR=document.asXML();
   // System.out.println(xmlConsultarCasoPQR);
	
	String XmlResCrearCasoPQR = super.httpostConsultaEM(xmlConsultarCasoPQR);
	XmlRespCaso consultadeCasoPQR = new XmlRespCaso();
	
	try {
		parametrosDatos  = consultadeCasoPQR.consultaCasoPRQ(XmlResCrearCasoPQR);
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println("RESPUESTA CONSULTA CASO\n"+XmlResCrearCasoPQR);
	
	return parametrosDatos;
}






	
}
