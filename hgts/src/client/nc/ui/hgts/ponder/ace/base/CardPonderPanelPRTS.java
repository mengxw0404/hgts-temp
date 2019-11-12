package nc.ui.hgts.ponder.ace.base;

import nc.bs.logging.Logger;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.*;
import nc.ui.pub.bill.*;
import nc.ui.pub.print.IDataSource;
import nc.vo.pub.lang.UFDouble;

public class CardPonderPanelPRTS
    implements IDataSource
{

    public CardPonderPanelPRTS(String m_sModuleName, BillCardPanel billcardpanel)
    {
        this.m_sModuleName = "";
        m_billcardpanel = null;
        m_billcardpanel = billcardpanel;
        this.m_sModuleName = m_sModuleName;
    }

    public String[] getAllDataItemExpress()
    {
        int headCount = 0;
        int bodyCount = 0;
        int tailCount = 0;
        if(m_billcardpanel.getHeadItems() != null)
            headCount = m_billcardpanel.getHeadItems().length;
        if(m_billcardpanel.getBillModel() != null && m_billcardpanel.getBodyItems() != null)
            bodyCount = m_billcardpanel.getBillModel().getBodyItems().length;
        if(m_billcardpanel.getTailItems() != null)
            tailCount = m_billcardpanel.getTailItems().length;
        int count = headCount + bodyCount + tailCount;
        String expfields[] = new String[count];
        try
        {
            for(int i = 0; i < headCount; i++)
                expfields[i] = (new StringBuilder()).append("h_").append(m_billcardpanel.getHeadItems()[i].getKey()).toString();

            for(int j = 0; j < bodyCount; j++)
                expfields[j + headCount] = m_billcardpanel.getBillModel().getBodyItems()[j].getKey();

            for(int k = 0; k < tailCount; k++)
                expfields[k + headCount + bodyCount] = (new StringBuilder()).append("t_").append(m_billcardpanel.getTailItems()[k].getKey()).toString();

        }
        catch(Throwable e)
        {
            Logger.error((new StringBuilder()).append(e.getMessage()).append("error at  getAllDataItemExpress()").toString());
        }
        return expfields;
    }

    public String[] getAllDataItemNames()
    {
        int headCount = 0;
        int bodyCount = 0;
        int tailCount = 0;
        if(m_billcardpanel.getHeadItems() != null)
            headCount = m_billcardpanel.getHeadItems().length;
        if(m_billcardpanel.getBillModel() != null && m_billcardpanel.getBodyItems() != null)
            bodyCount = m_billcardpanel.getBillModel().getBodyItems().length;
        if(m_billcardpanel.getTailItems() != null)
            tailCount = m_billcardpanel.getTailItems().length;
        int count = headCount + bodyCount + tailCount;
        String namefields[] = new String[count];
        try
        {
            for(int i = 0; i < headCount; i++)
                namefields[i] = m_billcardpanel.getHeadItems()[i].getName();

            for(int j = 0; j < bodyCount; j++)
                namefields[j + headCount] = m_billcardpanel.getBillModel().getBodyItems()[j].getName();

            for(int k = 0; k < tailCount; k++)
                namefields[k + headCount + bodyCount] = m_billcardpanel.getTailItems()[k].getName();

        }
        catch(Throwable e)
        {
            Logger.error((new StringBuilder()).append(e.getMessage()).append("error at  getAllDataItemNames()").toString());
        }
        return namefields;
    }

    public String[] getDependentItemExpressByExpress(String itemName)
    {
        return null;
    }

    public String[] getItemValuesByExpress(String itemExpress)
    {
        int bodyCount = 0;
        int rowCount = 0;
        int headCount = 0;
        BillItem item = null;
        String sc;
        String sr;
        String wb;

        UIRefPane item_h;
        String rslt[];
        UFDouble value;
        int j;
        
        if(m_billcardpanel.getHeadItems() != null)
            headCount = m_billcardpanel.getHeadItems().length;
        if(m_billcardpanel.getBillModel() != null && m_billcardpanel.getBillModel().getBodyItems() != null)
            bodyCount = m_billcardpanel.getBillModel().getBodyItems().length;
        if(m_billcardpanel.getTailItems() != null)
        	rowCount = m_billcardpanel.getRowCount();
        
        if(itemExpress.startsWith("hgts_invoicesheet_b.")){
        	rslt = new String[rowCount];
    		for(int i = 0; i< bodyCount; i++){
            	item = m_billcardpanel.getBillModel().getBodyItems()[i];
            	if(itemExpress.substring(20).equals(item.getKey())){
            		if(item.getDataType() == 4){
            			for(j = 0; j < rowCount; j++)
            				if(m_billcardpanel.getBodyValueAt(j, item.getKey()) == null)
            					rslt[j] = NCLangRes.getInstance().getStrByID("uifactory", "UPPuifactory-000165");
            				else if(m_billcardpanel.getBodyValueAt(j, item.getKey()).toString().equals("false"))
            					rslt[j] = NCLangRes.getInstance().getStrByID("uifactory", "UPPuifactory-000165");
                 	        else
                 	        	rslt[j] = NCLangRes.getInstance().getStrByID("uifactory", "UPPuifactory-000164");

            		}else{
            			for(j = 0; j < rowCount; j++){
            				rslt[j] = m_billcardpanel.getBodyValueAt(j, item.getKey()) != null ? m_billcardpanel.getBodyValueAt(j, item.getKey()).toString() : "";
            			}
            			return rslt;
            		}
            	}
            }
        }
         
        item = m_billcardpanel.getHeadItem(itemExpress);
    	if(item == null){
    		return null;
    	}
    	if(item.getKey().equals(itemExpress)){
    		 wb = ((UIRefPane)item.getComponent()).getText();
    		if(item.getValue() == null)
        		return (new String[] { NCLangRes.getInstance().getStrByID("uifactory", "UPPuifactory-000165")});
        	if(item.getValue().equals("false"))
        		return (new String[] { NCLangRes.getInstance().getStrByID("uifactory", "UPPuifactory-000165") });
        	if(item.getDataType() == 6)
            {
                sc = ((UIComboBox)item.getComponent()).getSelectedItem().toString();
                return (new String[] {  sc });
            }
            if(item.getDataType() == 5)
            {
                sr = ((UIRefPane)item.getComponent()).isReturnCode() ? ((UIRefPane)item.getComponent()).getRefCode() : ((UIRefPane)item.getComponent()).getRefName();
                return (new String[] {    sr   });
            }
            if(item.getDataType() == 9)
            {
                wb = ((UITextAreaScrollPane)item.getComponent()).getText();
                return (new String[] {   wb });
            }
           
            if(item.getDataType() == 2)
            {
                item_h = (UIRefPane)item.getComponent();
                value = new UFDouble(wb);
                value = value.setScale(item_h.getNumPoint(), 4);
                wb = value.toString();
                return (new String[] {   wb });
            }
            return (new String[] {   wb });
        }
      /*  if(itemExpress.startsWith("hgts_invoicesheet.")){

        	item = m_billcardpanel.getHeadItem(itemExpress.substring(18));
        	if(item == null)
        		return null;
        	if(!item.getKey().equals(itemExpress.substring(18)))
        		//
        	if(item.getDataType() != 4)
     //       break MISSING_BLOCK_LABEL_220;
        	if(item.getValue() == null)
        		return (new String[] { NCLangRes.getInstance().getStrByID("uifactory", "UPPuifactory-000165")});
        	if(item.getValue().equals("false"))
        		return (new String[] { NCLangRes.getInstance().getStrByID("uifactory", "UPPuifactory-000165") });
        	if(item.getDataType() == 6)
            {
                sc = ((UIComboBox)item.getComponent()).getSelectedItem().toString();
                return (new String[] {  sc });
            }
            if(item.getDataType() == 5)
            {
                sr = ((UIRefPane)item.getComponent()).isReturnCode() ? ((UIRefPane)item.getComponent()).getRefCode() : ((UIRefPane)item.getComponent()).getRefName();
                return (new String[] {    sr   });
            }
            if(item.getDataType() == 9)
            {
                wb = ((UITextAreaScrollPane)item.getComponent()).getText();
                return (new String[] {   wb });
            }
            wb = ((UIRefPane)item.getComponent()).getText();
            if(item.getDataType() == 2)
            {
                item_h = (UIRefPane)item.getComponent();
                value = new UFDouble(wb);
                value = value.setScale(item_h.getNumPoint(), 4);
                wb = value.toString();
                return (new String[] {   wb });
            }
        }
        rslt = new String[rowCount];
        for(int i = 0; i< bodyCount; i++){
        	item = m_billcardpanel.getBillModel().getBodyItems()[i];
        	if(itemExpress.contains(item.getKey())){
        		if(item.getDataType() == 4){
        			for(j = 0; j < rowCount; j++)
        				if(m_billcardpanel.getBodyValueAt(j, item.getKey()) == null)
        					rslt[j] = NCLangRes.getInstance().getStrByID("uifactory", "UPPuifactory-000165");
        				else if(m_billcardpanel.getBodyValueAt(j, item.getKey()).toString().equals("false"))
        					rslt[j] = NCLangRes.getInstance().getStrByID("uifactory", "UPPuifactory-000165");
             	        else
             	        	rslt[j] = NCLangRes.getInstance().getStrByID("uifactory", "UPPuifactory-000164");

        		}else{
        			for(j = 0; j < rowCount; j++){
        				rslt[j] = m_billcardpanel.getBodyValueAt(j, item.getKey()) != null ? m_billcardpanel.getBodyValueAt(j, item.getKey()).toString() : "";
        			}
        			return rslt;
        		}
        	}
        }*/
       return null;
   
    }

    public String getModuleName()
    {
        return m_sModuleName;
    }

    public boolean isNumber(String itemExpress)
    {
        BillItem item = null;
        if(!itemExpress.startsWith("h_"))
         //   break ;//MISSING_BLOCK_LABEL_49;
        item = m_billcardpanel.getHeadItem(itemExpress.substring(2));
        if(item == null)
            return false;
        BillItem items[];
 
        int i;
        try
        {
            if(item.getDataType() == 1 || item.getDataType() == 2)
                return true;
        }
        catch(Throwable e)
        {
            Logger.error((new StringBuilder()).append(e.getMessage()).append("error at  isNumber()").toString());
            return false;
        }
      //  break MISSING_BLOCK_LABEL_222;
        if(!itemExpress.startsWith("t_"))
           // break MISSING_BLOCK_LABEL_98;
        item = m_billcardpanel.getTailItem(itemExpress.substring(2));
        if(item == null)
            return false;
        if(item.getDataType() == 1 || item.getDataType() == 2)
            return true;
       // break MISSING_BLOCK_LABEL_222;
        if(m_billcardpanel.getBillModel() == null)
            return false;
        items = m_billcardpanel.getBillModel().getBodyItems();
        item = null;
        i = 0;
        do
        {
            if(i >= items.length)
                break;
            if(items[i].getKey().equals(itemExpress))
            {
                item = items[i];
                break;
            }
            i++;
        } while(true);
        if(item == null)
            return false;
        if(item == null)
            return false;
        if(item.getDataType() == 1 || item.getDataType() == 2)
            return true;
        return false;
    }

    private String m_sModuleName;
    private BillCardPanel m_billcardpanel;

}

