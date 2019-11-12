package nc.tc.nc.vo.hgts.temphvo;
import org.testng.*;
import nc.vo.pub.*;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;
import java.util.ArrayList;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.io.Serializable;
import jxl.read.biff.BiffException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.yonyou.uat.util.ExcelDataProvider;
import com.yonyou.uat.util.DBDataProvider;
import com.yonyou.uat.dbmanagement.DBManage;
import com.yonyou.uat.dbmanagement.QueryInfoVO;
import nc.vo.hgts.temphvo.TempB;
import nc.bs.framework.common.NCLocator;
import com.yonyou.uat.framework.BaseTestCase;
import java.lang.String;
import java.lang.String;
import java.lang.String;
import java.lang.String;
import java.lang.String;
import nc.vo.pub.IVOMeta;
public class TempBTest extends BaseTestCase {
  TempB tempB=null;
  DBManage dbManage=null;
  
  @BeforeClass 
  public void BeforeClass(){
    tempB=NCLocator.getInstance().lookup(TempB.class);
  }
  
  @AfterClass 
  public void AfterClass(){
  }
  
  @BeforeMethod 
  public void BeforeMethod(){
    List<String> tableList=new ArrayList<String>();
    tableList.add("pub_wfexptlog");
    dbManage=new DBManage();
    dbManage.setTableList(tableList);
    dbManage.tableExport();
  }
  
  @AfterMethod 
  public void AfterMethod(){
    dbManage.tableRollBack();
  }
  
  @Test(description="",dependsOnMethods={},groups="",timeOut=100000,dataProvider="dp") 
  public void getId(  Map<String,ArrayList<String>> dp){
    
    //Construct method parameters
    
    //Invoke tested method
    String retObj="";
    retObj=tempB.getId();
    
    //Verify result is ok
    
    //Verify Object1 == Object2
    Assert.assertEquals(retObj,"expectValue");
    
    //Verify DB result is ok
    QueryInfoVO queryInfoVerify=new QueryInfoVO();
    queryInfoVerify.setDatasource("datasourceName");
    queryInfoVerify.setTableName("tableName");
    queryInfoVerify.setCondition("where condition");
    List<Object> actualObjects=super.getDBObjectClass(Object.class,queryInfoVerify);
    Object actualObject=(Object)actualObjects.get(0);
    Assert.assertEquals("actualObject.getxxx()",dp.get("colName").get(0));
    
    //Verify whether have exception information in log 
    super.verifyLog("Error key word");
  }
  
  @Test(description="",dependsOnMethods={},groups="",timeOut=100000,dataProvider="dp") 
  public void setId(  Map<String,ArrayList<String>> dp){
    
    //Construct method parameters
    String newId=(String)dp.get("newid").get(0);
    
    //Invoke tested method
    tempB.setId(newId);
    
    //Verify result is ok
    
    //Verify Object1 == Object2
    Assert.assertEquals("actual","expected");
    Assert.assertNotNull("actual");
    
    //Verify DB result is ok
    QueryInfoVO queryInfoVerify=new QueryInfoVO();
    queryInfoVerify.setDatasource("datasourceName");
    queryInfoVerify.setTableName("tableName");
    queryInfoVerify.setCondition("where condition");
    List<Object> actualObjects=super.getDBObjectClass(Object.class,queryInfoVerify);
    Object actualObject=(Object)actualObjects.get(0);
    Assert.assertEquals("actualObject.getxxx()",dp.get("colName").get(0));
    
    //Verify whether have exception information in log 
    super.verifyLog("Error key word");
  }
  
  @Test(description="",dependsOnMethods={},groups="",timeOut=100000,dataProvider="dp") 
  public void getParentPKFieldName(  Map<String,ArrayList<String>> dp){
    
    //Construct method parameters
    
    //Invoke tested method
    String retObj="";
    retObj=tempB.getParentPKFieldName();
    
    //Verify result is ok
    
    //Verify Object1 == Object2
    Assert.assertEquals(retObj,"expectValue");
    
    //Verify DB result is ok
    QueryInfoVO queryInfoVerify=new QueryInfoVO();
    queryInfoVerify.setDatasource("datasourceName");
    queryInfoVerify.setTableName("tableName");
    queryInfoVerify.setCondition("where condition");
    List<Object> actualObjects=super.getDBObjectClass(Object.class,queryInfoVerify);
    Object actualObject=(Object)actualObjects.get(0);
    Assert.assertEquals("actualObject.getxxx()",dp.get("colName").get(0));
    
    //Verify whether have exception information in log 
    super.verifyLog("Error key word");
  }
  
  @Test(description="",dependsOnMethods={},groups="",timeOut=100000,dataProvider="dp") 
  public void getPKFieldName(  Map<String,ArrayList<String>> dp){
    
    //Construct method parameters
    
    //Invoke tested method
    String retObj="";
    retObj=tempB.getPKFieldName();
    
    //Verify result is ok
    
    //Verify Object1 == Object2
    Assert.assertEquals(retObj,"expectValue");
    
    //Verify DB result is ok
    QueryInfoVO queryInfoVerify=new QueryInfoVO();
    queryInfoVerify.setDatasource("datasourceName");
    queryInfoVerify.setTableName("tableName");
    queryInfoVerify.setCondition("where condition");
    List<Object> actualObjects=super.getDBObjectClass(Object.class,queryInfoVerify);
    Object actualObject=(Object)actualObjects.get(0);
    Assert.assertEquals("actualObject.getxxx()",dp.get("colName").get(0));
    
    //Verify whether have exception information in log 
    super.verifyLog("Error key word");
  }
  
  @Test(description="",dependsOnMethods={},groups="",timeOut=100000,dataProvider="dp") 
  public void getTableName(  Map<String,ArrayList<String>> dp){
    
    //Construct method parameters
    
    //Invoke tested method
    String retObj="";
    retObj=tempB.getTableName();
    
    //Verify result is ok
    
    //Verify Object1 == Object2
    Assert.assertEquals(retObj,"expectValue");
    
    //Verify DB result is ok
    QueryInfoVO queryInfoVerify=new QueryInfoVO();
    queryInfoVerify.setDatasource("datasourceName");
    queryInfoVerify.setTableName("tableName");
    queryInfoVerify.setCondition("where condition");
    List<Object> actualObjects=super.getDBObjectClass(Object.class,queryInfoVerify);
    Object actualObject=(Object)actualObjects.get(0);
    Assert.assertEquals("actualObject.getxxx()",dp.get("colName").get(0));
    
    //Verify whether have exception information in log 
    super.verifyLog("Error key word");
  }
  
  @Test(description="",dependsOnMethods={},groups="",timeOut=100000,dataProvider="dp") 
  public void getDefaultTableName(  Map<String,ArrayList<String>> dp){
    
    //Construct method parameters
    
    //Invoke tested method
    String retObj="";
    retObj=TempB.getDefaultTableName();
    
    //Verify result is ok
    
    //Verify Object1 == Object2
    Assert.assertEquals(retObj,"expectValue");
    
    //Verify DB result is ok
    QueryInfoVO queryInfoVerify=new QueryInfoVO();
    queryInfoVerify.setDatasource("datasourceName");
    queryInfoVerify.setTableName("tableName");
    queryInfoVerify.setCondition("where condition");
    List<Object> actualObjects=super.getDBObjectClass(Object.class,queryInfoVerify);
    Object actualObject=(Object)actualObjects.get(0);
    Assert.assertEquals("actualObject.getxxx()",dp.get("colName").get(0));
    
    //Verify whether have exception information in log 
    super.verifyLog("Error key word");
  }
  
  @Test(description="",dependsOnMethods={},groups="",timeOut=100000,dataProvider="dp") 
  public void getMetaData(  Map<String,ArrayList<String>> dp){
    
    //Construct method parameters
    
    //Invoke tested method
    IVOMeta retObj=null;
    retObj=tempB.getMetaData();
    
    //Verify result is ok
    
    //Verify Object1 == Object2
    Assert.assertNotNull(retObj);
    Assert.assertNotNull(retObj.getAttributes());
    Assert.assertNotNull(retObj.getBusinessAttribute());
    Assert.assertEquals(retObj.getBusinessAttribute().size(),0);
    Assert.assertNotNull(retObj.getEntityName());
    Assert.assertEquals(retObj.getEntityName(),"expectValue");
    Assert.assertNotNull(retObj.getLabel());
    Assert.assertEquals(retObj.getLabel(),"expectValue");
    Assert.assertNotNull(retObj.getPrimaryAttribute());
    Assert.assertNotNull(retObj.getStatisticInfo());
    
    //Verify DB result is ok
    QueryInfoVO queryInfoVerify=new QueryInfoVO();
    queryInfoVerify.setDatasource("datasourceName");
    queryInfoVerify.setTableName("tableName");
    queryInfoVerify.setCondition("where condition");
    List<Object> actualObjects=super.getDBObjectClass(Object.class,queryInfoVerify);
    Object actualObject=(Object)actualObjects.get(0);
    Assert.assertEquals("actualObject.getxxx()",dp.get("colName").get(0));
    
    //Verify whether have exception information in log 
    super.verifyLog("Error key word");
  }
}
