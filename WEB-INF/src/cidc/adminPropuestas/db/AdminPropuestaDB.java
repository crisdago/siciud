package cidc.adminPropuestas.db;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import cidc.general.asigPares.db.AsignacionParesDB;
import cidc.general.db.BaseDB;
import cidc.general.db.CursorDB;
import cidc.general.mails.EnvioMail2;
import cidc.general.mails.Reporte;
import cidc.general.obj.CrearClaves;
import cidc.general.obj.Globales;
import cidc.inscripcionConv.obj.CoInvestigadorOBJ;
import cidc.inscripcionConv.obj.CompromisosOBJ;
import cidc.inscripcionConv.obj.InscripcionConvOBJ;
import cidc.inscripcionConv.obj.ResumenInscOBJ;
import cidc.inscripcionConv.obj.ResumenRubrosOBJ;
import cidc.inscripcionConv.obj.ResumenCompromOBJ;
import cidc.adminArticulos.obj.Articulo;
import cidc.adminEvaluador.obj.AreasTrabOBJ;
import cidc.adminPropuestas.obj.DatEvaluadorOBJ;
import cidc.adminPropuestas.obj.DatoConvocatoriaOBJ;
import cidc.adminPropuestas.obj.EstadoPropuestaOBJ;
import cidc.adminPropuestas.obj.FiltroEvaluadorOBJ;
import cidc.adminPropuestas.obj.ListaEvaluadorOBJ;
import cidc.adminPropuestas.obj.PropuestaOBJ;
import cidc.adminPropuestas.obj.ResParcial;


public class AdminPropuestaDB extends BaseDB{

	public AdminPropuestaDB(CursorDB c, int perfil) {
		super(c, perfil);
		// TODO Auto-generated constructor stub
		rb=ResourceBundle.getBundle("cidc.adminPropuestas.consultas");
	}

	public List getPropuestas(int ano,int numero,String estado){
		List l=new ArrayList();
		Connection cn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("getPropuestas"));
			ps.setLong(i++,ano);
			ps.setLong(i++,numero);
			ps.setBoolean(i++,Boolean.parseBoolean(estado));
			rs=ps.executeQuery();
		//	System.out.println("");
			while(rs.next()){
				i=1;
				PropuestaOBJ propuestaOBJ=new PropuestaOBJ();
				propuestaOBJ.setConvAbierta(rs.getBoolean(i++));
				propuestaOBJ.setCodPropuesta(rs.getLong(i++));
				propuestaOBJ.setNomPropuesta(rs.getString(i++));
				propuestaOBJ.setEstadoEvalInt(rs.getInt(i++));
				propuestaOBJ.setEstadoEvalExt(rs.getInt(i++));
				propuestaOBJ.setEstadoEvalComit(rs.getInt(i++));
				propuestaOBJ.setPropActiva(rs.getBoolean(i++));
				l.add(propuestaOBJ);
			}
		}catch (SQLException e) {
			lanzaExcepcion(e);
		}catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(rs);
			cerrar(ps);
			cerrar(cn);
		}
		return l;
	}

	public List ajaxNumConvocat(int conv) {
		// TODO Auto-generated method stub
		List numeros=new ArrayList();
		Connection cn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		int i=0;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("ajaxNum_Conv"));
			ps.setLong(1,conv);
			rs=ps.executeQuery();
			while(rs.next()){
				numeros.add(rs.getString(1));
			}
		} catch (SQLException e) {
			lanzaExcepcion(e);
		}catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(rs);
			cerrar(ps);
			cerrar(cn);
		}
		return numeros;
	}

	public Object ajaxConv() {
		List convocatoria=new ArrayList();
		Connection cn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		int i=0;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("ajaxAnos_Conv"));
			rs=ps.executeQuery();
			while(rs.next()){
				convocatoria.add(rs.getString(1));
			}
		} catch (SQLException e) {
			lanzaExcepcion(e);
		}catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(rs);
			cerrar(ps);
			cerrar(cn);
		}
		return convocatoria;
	}

	public DatoConvocatoriaOBJ estadoConvocat(int conv, int num) {
		// TODO Auto-generated method stub
		List numeros=new ArrayList();
		Connection cn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		DatoConvocatoriaOBJ convocatoriaOBJ=null;
		int i=0;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("estado_Conv"));
			ps.setLong(1,conv);
			ps.setLong(2,num);
			rs=ps.executeQuery();
			while(rs.next()){
				convocatoriaOBJ=new DatoConvocatoriaOBJ();
				convocatoriaOBJ.setCodConvocatoria(rs.getString(1));
				convocatoriaOBJ.setEstado(rs.getString(2));
				numeros.add(convocatoriaOBJ);
			}
		} catch (SQLException e) {
			lanzaExcepcion(e);
		}catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(rs);
			cerrar(ps);
			cerrar(cn);
		}
		return convocatoriaOBJ;
	}


	public EstadoPropuestaOBJ estEvalProp(long prop, int tipoEval){
		Connection cn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		EstadoPropuestaOBJ estadoPropuestaOBJ=new EstadoPropuestaOBJ();
		int i=1;
		String fecha="";
		String []archivos=null;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("getInfoPropuesta"+tipoEval));
			ps.setLong(1,prop);
			ps.setLong(2,tipoEval);
			rs=ps.executeQuery();
			archivos=getNombreArchivo(cn,prop);
			estadoPropuestaOBJ.setArchivo(archivos[0]);
			estadoPropuestaOBJ.setAnexos(archivos[1]);
			while(rs.next()){
				i=1;
				estadoPropuestaOBJ.setIdEvalAceptado(rs.getString(i++));
				fecha=rs.getString(i++);
			//	System.out.println(fecha);
				if(fecha!=null)
				if(fecha.contains("."))
					fecha=fecha.replace('-','/').substring(0,fecha.lastIndexOf('.'));
				estadoPropuestaOBJ.setFechaLoginPsw(fecha);
				estadoPropuestaOBJ.setFechaDocs(rs.getString(i++));
				estadoPropuestaOBJ.setFechaEval(rs.getString(i++));
			//	System.out.println("dato fecha="+estadoPropuestaOBJ.getFechaEval());
			}
			getNombrePropuesta(cn,estadoPropuestaOBJ,prop);
			estadoPropuestaOBJ.setListaEvaluador(getListaEval(cn,prop,tipoEval));
		}catch (SQLException e) {
			lanzaExcepcion(e);
		}catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(rs);
			cerrar(ps);
			cerrar(cn);
		}
		return estadoPropuestaOBJ;
	}
	public EstadoPropuestaOBJ getNombrePropuesta( Connection cn, EstadoPropuestaOBJ estadoPropuestaOBJ, long prop){
		PreparedStatement ps=null;
		ResultSet rs=null;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("getNombrePropuesta"));
			ps.setLong(1,prop);
			rs=ps.executeQuery();
			while(rs.next()){
				i=1;
				estadoPropuestaOBJ.setCodInvest(rs.getString(i++));
				estadoPropuestaOBJ.setNombrePropuesta(rs.getString(i++));
				estadoPropuestaOBJ.setNombreInvest(rs.getString(i++));
			}
		}catch (SQLException e) {
			lanzaExcepcion(e);
		}catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(rs);
			cerrar(ps);
		}
		return estadoPropuestaOBJ;
	}

	private List getListaEval(Connection cn, long prop, int tipoEval) throws SQLException{
		List listaEval=new ArrayList();
		PreparedStatement ps=null;
		ResultSet rs=null;
		ListaEvaluadorOBJ listaEvaluadorOBJ=null;
		int i=1;
		try {
			ps=cn.prepareStatement(rb.getString("getEvalAsignados"));
			ps.setLong(1,prop);
			ps.setLong(2,tipoEval);
			rs=ps.executeQuery();

			while(rs.next()){
				i=1;
				listaEvaluadorOBJ=new ListaEvaluadorOBJ();
				listaEvaluadorOBJ.setCodEval(rs.getLong(i++));
				listaEvaluadorOBJ.setNombreEval(rs.getString(i++));
				listaEvaluadorOBJ.setFechaAsig(rs.getString(i++));
				listaEvaluadorOBJ.setFechaResp(rs.getString(i++));
				listaEvaluadorOBJ.setFechaCancela(rs.getString(i++));
				listaEvaluadorOBJ.setEstado(rs.getInt(i++));
				listaEval.add(listaEvaluadorOBJ);
			}
		}catch (SQLException e) {
			lanzaExcepcion(e);
		}catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(rs);
			cerrar(ps);
		}
		return listaEval;
	}

	public boolean asignaRespuesta(long prop, int resp, String codEval, int tipoEval) {
		boolean retorno=false;
		Connection cn=null;
		PreparedStatement ps=null;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("asignaRespuesta"));
		//	System.out.println("respuesta="+resp);
			ps.setInt(i++,resp);
			ps.setLong(i++,prop);
			ps.setInt(i++,Integer.parseInt(codEval));
			ps.executeUpdate();
			ps=cn.prepareStatement(rb.getString("cambia_estado"+tipoEval));
			if(resp==1)
				ps.setInt(1,3);
			else
				ps.setInt(1,1);
			ps.setLong(2,prop);
			ps.executeUpdate();
			retorno=true;
		}catch (SQLException e) {
			lanzaExcepcion(e);
		}catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return retorno;
	}

	public boolean cancelaEval(long prop, String codEval,int tipoEval) {
		boolean retorno=false;
		Connection cn=null;
		PreparedStatement ps=null;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("cancelaEvaluador"));
			ps.setLong(i++,prop);
			ps.setString(i++,codEval);
			ps.executeUpdate();

			ps=cn.prepareStatement(rb.getString("cambia_estado"+tipoEval));
			ps.setInt(1,1);
			ps.setLong(2,prop);
			ps.executeUpdate();

			ps=cn.prepareStatement(rb.getString("quitaFechaMail"+tipoEval));
			ps.setLong(1,prop);
			ps.executeUpdate();
			retorno=true;
		}catch (SQLException e) {
			lanzaExcepcion(e);
		}catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return retorno;
	}

	public boolean asignaEvaluador(String prop, String codEval,String tipoEval) {
		boolean retorno=false;
		AsignacionParesDB asignacionParesDB=new AsignacionParesDB(super.cursor,super.perfil);
		retorno=asignacionParesDB.asignaEvaluadorPropuesta(prop,codEval,tipoEval);
		return retorno;
	}

	public List filtraEvaluador(FiltroEvaluadorOBJ filtro) {
		AsignacionParesDB asigancion=new AsignacionParesDB(super.cursor,super.perfil);
		List lista=asigancion.filtro(filtro);
		return lista;
	}

	public DatEvaluadorOBJ getDatosEvaluador(String codEval, int tipoEval){
		AsignacionParesDB asigancion=new AsignacionParesDB(super.cursor,super.perfil);
		DatEvaluadorOBJ datEvaluadorOBJ=asigancion.datosEvaluador(codEval, tipoEval);
		return datEvaluadorOBJ;
	}

	public String[] getNombreArchivo(Connection cn,long propuesta) {
		String []archivo=new String[2];
		PreparedStatement ps=null;
		ResultSet rs=null;
	//	System.out.println("propuesta=="+propuesta);
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("getNombreArchivos"));
			ps.setLong(1,propuesta);
			rs=ps.executeQuery();
			while(rs.next()){
				archivo[0]=rs.getString(1);
				archivo[1]=rs.getString(2);
			}
	//	System.out.println("archivo=="+archivo);
		} catch (SQLException e) {
			lanzaExcepcion(e);
		} catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(rs);
			cerrar(ps);
		}
		return archivo;
	}

	public boolean entregaDocs(long prop, int tipoEval) {
		Connection cn=null;
		PreparedStatement ps=null;
		boolean retorno=false;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("entregaDocs"+tipoEval));
			ps.setLong(1,prop);
			ps.executeUpdate();

			ps=cn.prepareStatement(rb.getString("cambia_estado"+tipoEval));
			ps.setInt(1,6);
			ps.setLong(2,prop);
			ps.executeUpdate();
			retorno=true;
		}catch (SQLException e) {
			lanzaExcepcion(e);
		} catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return retorno;
	}
	public ResumenInscOBJ getResumenInscripcion(String idProp) {
		Connection cn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ResumenInscOBJ resumenInscOBJ=null;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("getResumenInscripcion"));
			ps.setLong(1,Long.parseLong(idProp));
			rs=ps.executeQuery();
			while(rs.next()){
				resumenInscOBJ=new ResumenInscOBJ();
				resumenInscOBJ.setConvocatoria(rs.getString(i++));
				resumenInscOBJ.setFacultad(rs.getString(i++));
				resumenInscOBJ.setGrupo(rs.getString(i++));
				resumenInscOBJ.setInvestigador(rs.getString(i++));
				resumenInscOBJ.setPropuesta(rs.getString(i++));
				resumenInscOBJ.setPropAbstract(rs.getString(i++));
				resumenInscOBJ.setPalClaves(rs.getString(i++));
			}
			if(resumenInscOBJ!=null){
		//		System.out.println("Entra a buscar co investigadores");
				List listConinv=new ArrayList();
				CoInvestigadorOBJ coInvestigadorOBJ;
				ps=cn.prepareStatement(rb.getString("getResumenCoInvestigadores"));
				ps.setLong(1,Long.parseLong(idProp));
				rs=ps.executeQuery();
				while(rs.next()){
		//			System.out.println("encuntra datos de co investigadores");
					i=1;
					coInvestigadorOBJ=new CoInvestigadorOBJ();
					coInvestigadorOBJ.setDocumento(rs.getString(i++));
					coInvestigadorOBJ.setNombres(rs.getString(i++));
					coInvestigadorOBJ.setApellidos(rs.getString(i++));
					coInvestigadorOBJ.setPapel(rs.getString(i++));
					coInvestigadorOBJ.setHoras(rs.getString(i++));
					listConinv.add(coInvestigadorOBJ);
				}
				resumenInscOBJ.setCoInvestigadores(listConinv);
			}
			if(resumenInscOBJ!=null){
		//		System.out.println("Entra a buscar rubros");
				List listRub=new ArrayList();
				ResumenRubrosOBJ resumenRubrosOBJ;
				ps=cn.prepareStatement(rb.getString("getResumenRubros"));
				ps.setLong(1,Long.parseLong(idProp));
				rs=ps.executeQuery();
				while(rs.next()){
					i=1;
	//				System.out.println("encuntra datos de rubros");
					resumenRubrosOBJ=new ResumenRubrosOBJ();
					resumenRubrosOBJ.setNombre(rs.getString(i++));
					resumenRubrosOBJ.setCidc(rs.getString(i++));
					resumenRubrosOBJ.setUd(rs.getString(i++));
					resumenRubrosOBJ.setContra(rs.getString(i++));

					listRub.add(resumenRubrosOBJ);
				}
				resumenInscOBJ.setRubros(listRub);
			}
			if(resumenInscOBJ!=null){
		//		System.out.println("Entra a buscar rubros");
				List listComp=new ArrayList();
				ResumenCompromOBJ compromOBJ;
				ps=cn.prepareStatement(rb.getString("getResumenCompromisos"));
				ps.setLong(1,Long.parseLong(idProp));
				rs=ps.executeQuery();
				while(rs.next()){
					i=1;
	//				System.out.println("encuntra datos de rubros");
					compromOBJ=new ResumenCompromOBJ();
					compromOBJ.setProducto(rs.getString(i++));
					compromOBJ.setIndicador(rs.getString(i++));
					compromOBJ.setCantidad(rs.getInt(i++));
					listComp.add(compromOBJ);
		//			System.out.println("encuntra compromisos del resumen");
				}
				resumenInscOBJ.setCompromisos(listComp);
			}
		}catch (SQLException e){
			lanzaExcepcion(e);
		}catch (Exception e){
			lanzaExcepcion(e);
		}finally{
			cerrar(rs);
			cerrar(ps);
			cerrar(cn);
		}
		return resumenInscOBJ;
	}

	public ResParcial resParcial(String prop) {
		Connection cn=null;
		PreparedStatement ps=null;
		ResultSet rs=null;
		ResParcial parcial=null;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("getInfoGeneral"));
			ps.setLong(1, Long.parseLong(prop));
			rs=ps.executeQuery();
			while(rs.next()){
				i=1;
				parcial=new ResParcial();
				parcial.setIdResultado(rs.getInt(i++));
				parcial.setIdConvocatoria(rs.getInt(i++));
				parcial.setIdPropuesta(rs.getInt(i++));
				parcial.setDirector(rs.getString(i++));
				parcial.setMail(rs.getString(i++));
				parcial.setPropuesta(rs.getString(i++));
				parcial.setDuracion(rs.getInt(i++));
				parcial.setGrupo(rs.getString(i++));
				parcial.setCorte(rs.getInt(i++));
				parcial.setRadicado(rs.getInt(i++));
				parcial.setObservaciones(rs.getString(i++));
			}
			if(parcial!=null){
				BigInteger cidc=BigInteger.valueOf(0);
				BigInteger ud=BigInteger.valueOf(0);
				BigInteger contra=BigInteger.valueOf(0);
				BigInteger total=BigInteger.valueOf(0);
				String dato="";
				ps=cn.prepareStatement(rb.getString("getResumenRubros"));
				ps.setString(1,prop);
				rs=ps.executeQuery();
				while(rs.next()){
					i=1;
					dato=rs.getString(2);
					if(dato!=null && !dato.equals(""))
						cidc=cidc.add(new BigInteger(dato));

					dato=rs.getString(3);
					if(dato!=null && !dato.equals(""))
						ud=ud.add(new BigInteger(dato));

					dato=rs.getString(4);
					if(dato!=null && !dato.equals(""))
						contra=contra.add(new BigInteger(dato));

				}
				total=total.add(cidc);
				total=total.add(ud);
				total=total.add(contra);

				parcial.setPresCIDC(""+cidc);
				parcial.setPresUD(""+ud);
				parcial.setPresCONTRA(""+contra);
				parcial.setPresTotal(""+total);

				List listaCoInvest=new ArrayList();
				CoInvestigadorOBJ coInvestigadorOBJ;
				ps=cn.prepareStatement(rb.getString("getResumenCoInvestigadores"));
				ps.setString(1,prop);
				rs=ps.executeQuery();
				while(rs.next()){
					i=1;
					coInvestigadorOBJ=new CoInvestigadorOBJ();
					coInvestigadorOBJ.setDocumento(rs.getString(i++));
					coInvestigadorOBJ.setNombres(rs.getString(i++));
					coInvestigadorOBJ.setApellidos(rs.getString(i++));
					coInvestigadorOBJ.setPapel(rs.getString(i++));
					listaCoInvest.add(coInvestigadorOBJ);
				}
				parcial.setListaCoInvest(listaCoInvest);
			}
		}catch (SQLException e) {
			lanzaExcepcion(e);
		} catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return parcial;
	}

	public boolean insertaRespParcial(String idProp,String radicado, String corte, String observaciones){
		Connection cn=null;
		PreparedStatement ps=null;
		boolean retorno=false;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("insertaRespParcial"));
			ps.setLong(1,Long.parseLong(idProp));
			ps.setLong(2,Long.parseLong(corte));
			ps.setLong(3,Long.parseLong(radicado));
			ps.setString(4,observaciones);
			ps.executeUpdate();
			retorno=true;
		}catch (SQLException e) {
			lanzaExcepcion(e);
		} catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return retorno;
	}
	public boolean modificaRespParcial(String idResp,String radicado, String corte, String observaciones){
		Connection cn=null;
		PreparedStatement ps=null;
		boolean retorno=false;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("modificaRespParcial"));
			ps.setLong(1,Long.parseLong(corte));
			ps.setLong(2,Long.parseLong(radicado));
			ps.setString(3,observaciones);
			ps.setLong(4,Long.parseLong(idResp));
			ps.executeUpdate();
			retorno=true;
		}catch (SQLException e) {
			lanzaExcepcion(e);
		} catch (Exception e) {
			lanzaExcepcion(e);
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return retorno;
	}


	public boolean activaPropuesta(long prop, int estadoActiva) {
		boolean retorno=false;
		Connection cn=null;
		PreparedStatement ps=null;
		int i=1;
		try {
			cn=cursor.getConnection(super.perfil);
			ps=cn.prepareStatement(rb.getString("cambiaActivacionPropuesta"));
			if(estadoActiva==1){
				ps.setBoolean(i++,true);
				setMensaje("Propuesta Activada satisfactoriamente");
			}
			else{
				ps.setBoolean(i++,false);
				setMensaje("Propuesta Desactivada satisfactoriamente");
			}
			ps.setLong(i++,prop);
			ps.executeUpdate();
			retorno=true;
		}catch (SQLException e) {
			lanzaExcepcion(e);
			setMensaje("La propuesta no pudo ser modificada");
		}catch (Exception e) {
			lanzaExcepcion(e);
			setMensaje("La propuesta no pudo ser modificada");
		}finally{
			cerrar(ps);
			cerrar(cn);
		}
		return retorno;
	}
}


