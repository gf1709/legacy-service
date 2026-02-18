package it.allitude.legacyserviceweb.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.google.type.DateTime;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ObjectDescription;
import com.ibm.as400.access.ObjectList;

import it.allitude.legacyserviceweb.DTOs.DSPOBJDRequestDTO;
import it.allitude.legacyserviceweb.DTOs.DSPOBJDResponseDTO;
import it.allitude.legacyserviceweb.DTOs.FFDResponseDTO;
import it.allitude.legacyserviceweb.DTOs.WRKOBJResponseDTO;
import it.allitude.legacyserviceweb.DTOs.WRKOBKRequestDTO;
import it.allitude.legacyserviceweb.db.ConnectionService;

@Component
public class ISeriesObjectUtil {

    private final ConnectionService _connectionService;
    private static final Logger log = LoggerFactory.getLogger(ISeriesSourceUtil.class);

    public ISeriesObjectUtil(ConnectionService anISeriesConnectionService) {
        this._connectionService = anISeriesConnectionService;
    }

    public ArrayList<LibraryListItem> GetLibraryList() throws Exception {
        ArrayList<LibraryListItem> res = new ArrayList<>();
        Connection con = _connectionService.getAS400JdbcConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT SCHEMA_NAME AS LIB, TYPE AS TYP, TEXT_DESCRIPTION AS DES FROM QSYS2.LIBRARY_LIST_INFO ORDER BY ORDINAL_POSITION");

        while (rs.next()) {
            String lib = rs.getString("LIB");
            String typ = rs.getString("TYP");
            String des = rs.getString("DES");
            LibraryListItem newEle = new LibraryListItem(lib, typ, des);
            res.add(newEle);
        }

        return res;
    }

    public void AddLibraryToLibraryList(String library) throws Exception {

        Connection con = _connectionService.getAS400JdbcConnection();
        java.sql.Statement stmt = con.createStatement();
        String sql = String.format("CALL QCMDEXC('ADDLIBLE %s')", library);
        stmt.executeUpdate(sql);
    }

    public void RemoveLibraryFromLibraryList(String library) throws Exception {

        Connection con = _connectionService.getAS400JdbcConnection();
        java.sql.Statement stmt = con.createStatement();
        String sql = String.format("CALL QCMDEXC('RMVLIBLE %s')", library);
        stmt.executeUpdate(sql);
    }
    private static final Integer MAX_CACHE_SIZE = 4000;
    private static final HashMap<String, FFDResponseDTO> _ffdCache = new HashMap<>();
    private static final HashMap<String, DSPOBJDResponseDTO> _objectDescriptionCache = new HashMap<>();

    public FFDResponseDTO getFFD(String library, String ddsName) throws Exception {
        String key = library.trim() + "." + ddsName.trim() + JSession.getCurrentSession().getJwt();
        // Una volta raggiunti i MAX_CACHE_SIZE elementi nella cache la ripulisco
        if (_ffdCache.keySet().size() > MAX_CACHE_SIZE) {
            _ffdCache.clear();
        }
        FFDResponseDTO res = _ffdCache.get(key);
        if (res != null) {
            return res;
        }
        Connection con = _connectionService.getAS400JdbcConnection();
        String bLibrary = library;
        // Se non specificata la libreria cerco la prima libreria che contiene l'oggetto
        if (bLibrary.length() < 1) {
            AS400 as = _connectionService.getAS400Connection();
            ObjectList libs = new ObjectList(as, ObjectList.LIBRARY_LIST, ddsName, ObjectList.ALL);
            libs.load();
            Enumeration<ObjectDescription> list = libs.getObjects();
            while (list.hasMoreElements()) {
                ObjectDescription o = (ObjectDescription) list.nextElement();

                String typ = o.getType().trim();
                if (typ.equals("FILE")) {
                    bLibrary = o.getLibrary().trim();
                    break;
                }
            }
            libs.close();
        }

        String sql = "SELECT DBIPOS, DBIFLD, DBILFL, DBIITP, CASE DBIITP WHEN 'L' THEN 10 WHEN 'T' THEN 8 ELSE DBIFLN END AS DBIFLN, DBINSC, DBITXT FROM QSYS.QADBILFI WHERE DBILIB=? AND DBIFIL=? ORDER BY DBIPOS";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, bLibrary);
        stmt.setString(2, ddsName);
        ResultSet rs = stmt.executeQuery();

        res = new FFDResponseDTO();
        res.setLibrary(bLibrary);
        res.setDdsName(ddsName);
        res.setIdf(getIdf(bLibrary, ddsName));

        while (rs.next()) {
            ISeriesFieldDescription fld = new ISeriesFieldDescription();
            fld.setFieldNo(Integer.parseInt(rs.getString(1)));
            fld.setFieldName(rs.getString(3));
            fld.setFieldType(rs.getString(4));
            fld.setFieldLength(Integer.parseInt(rs.getString(5)));
            int scale = (rs.getString(6) == null) ? -1 : Integer.parseInt(rs.getString(6));
            fld.setFieldScale(scale);
            fld.setFieldDescription(rs.getString(7));
            res.getFields().add(fld);

            String keyFieldDes = ddsName.trim() + "." + fld.getFieldName().trim();
            if (!_fieldDescriptionCache.containsKey(keyFieldDes)) {
                _fieldDescriptionCache.put(keyFieldDes, fld.getFieldDescription());
            }
        }
        _ffdCache.put(key, res);
        return res;
    }

    private String getIdf(String library, String ddsName) throws Exception {
        String sql = String.format("SELECT FILE_LEVEL_ID FROM QSYS2.SYSFILES WHERE SYSTEM_TABLE_SCHEMA = '%s' AND SYSTEM_TABLE_NAME = '%s'", library, ddsName);
        Connection con = _connectionService.getAS400JdbcConnection();
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            return rs.getString("FILE_LEVEL_ID");
        }
        return "";
    }

    private static final HashMap<String, String> _fieldDescriptionCache = new HashMap<>();

    String getFiedDescription(String ddsName, String fieldName) throws Exception {

        String keyFieldDes = ddsName.trim() + "." + fieldName.trim() + JSession.getCurrentSession().getJwt();
        // Una volta raggiunti i MAX_CACHE_SIZE elementi nella cache la ripulisco
        if (_fieldDescriptionCache.keySet().size() > MAX_CACHE_SIZE) {
            _fieldDescriptionCache.clear();
        }
        String res = _fieldDescriptionCache.get(keyFieldDes);
        if (res != null) {
            return res;
        }

        Connection con = _connectionService.getAS400JdbcConnection();

        String sql = "SELECT DBITXT FROM QSYS.QADBILFI WHERE DBIFIL=? AND DBIFLD=? LIMIT 1";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, ddsName);
        stmt.setString(2, fieldName);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            res = rs.getString(1);
        }
        _fieldDescriptionCache.put(keyFieldDes, res);
        return res;
    }

    public List<WRKOBJResponseDTO> wrkobj(WRKOBKRequestDTO wrkobjInfo) throws Exception {

        List<WRKOBJResponseDTO> res = new ArrayList<WRKOBJResponseDTO>();
        AS400 as = _connectionService.getAS400Connection();
        // ObjectList libs = new ObjectList(as, "QSYS", ObjectList.ALL, "*LIB" );
        // ObjectList libs = new ObjectList(as, "FC0382", ObjectList.ALL, ObjectList.ALL
        // // );

        if (!wrkobjInfo.isValid()) {
            throw new Exception("Specificare il nome della libreria o il nome oggetto");
        }

        ObjectList libs = new ObjectList(as, wrkobjInfo.getLibrary(), wrkobjInfo.getObjectName(),
                wrkobjInfo.getObjectType());
        libs.load();
        Enumeration<ObjectDescription> list = libs.getObjects();

        while (list.hasMoreElements()) {
            ObjectDescription o = (ObjectDescription) list.nextElement();
            WRKOBJResponseDTO objDes = new WRKOBJResponseDTO(o);
            res.add(objDes);
        }
        libs.close();
        return res;
    }

    public DSPOBJDResponseDTO dspobjd(DSPOBJDRequestDTO dspobjdInfo) throws Exception {
        String key = dspobjdInfo.getLibrary() + dspobjdInfo.getObjectName() + dspobjdInfo.getObjectType();
        if (_objectDescriptionCache.containsKey(key)) {
            return _objectDescriptionCache.get(key);
        }

        DSPOBJDResponseDTO res = new DSPOBJDResponseDTO(dspobjdInfo.getLibrary(), dspobjdInfo.getObjectName(),
                dspobjdInfo.getObjectType());
        AS400 as = _connectionService.getAS400Connection();

        if (!dspobjdInfo.isValid()) {
            throw new Exception("Specificare il nome della libreria o il nome oggetto");
        }

        ObjectDescription objDes = new ObjectDescription(as, dspobjdInfo.getLibrary(), dspobjdInfo.getObjectName(),
                dspobjdInfo.getObjectType());
        if (objDes.exists()) {
            objDes.refresh();
            res.setAttribute(objDes.getValueAsString(ObjectDescription.EXTENDED_ATTRIBUTE));
            res.setChangeDate((Date) objDes.getValue(ObjectDescription.CHANGE_DATE));
            res.setChangeUser(objDes.getValueAsString(ObjectDescription.USER_CHANGED));
            res.setCreationDate((Date) objDes.getValue(ObjectDescription.CREATION_DATE));
            res.setCreationUser(objDes.getValueAsString(ObjectDescription.CREATOR_USER_PROFILE));
            res.setDescription(objDes.getValueAsString(ObjectDescription.TEXT_DESCRIPTION));
            res.setOwner(objDes.getValueAsString(ObjectDescription.OWNER));
            res.setSize(objDes.getValueAsString(ObjectDescription.OBJECT_SIZE));
        }
        _objectDescriptionCache.put(key, res);
        return res;
    }
}
