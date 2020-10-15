package com.coffer.businesses.modules.weChat.v03.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffer.businesses.modules.weChat.WeChatConstant;
import com.coffer.businesses.modules.weChat.v03.dao.GuestDao;
import com.coffer.businesses.modules.weChat.v03.entity.Guest;
import com.coffer.core.common.persistence.Page;
import com.coffer.core.common.service.CrudService;


/**
 * 客户授权Service
 *
 * @author qipeihong
 * @version 2017-04-18
 */
@Service
@Transactional(readOnly = true)
public class GuestService extends CrudService<GuestDao, Guest> {

    //客户管理dao接口
    @Autowired
    private GuestDao guestdao;

    public Guest get(String id) {
        return super.get(id);
    }

    public List<Guest> findList(Guest Guest) {
        return super.findList(Guest);
    }

    public Page<Guest> findPage(Page<Guest> page, Guest Guest) {
        return super.findPage(page, Guest);
    }

    @Transactional(readOnly = false)
    public void save(Guest Guest) {
        super.save(Guest);
    }

    @Transactional(readOnly = false)
    public void delete(Guest Guest) {
        super.delete(Guest);
    }

    public Guest getByopenID(Guest guest) {
        Guest tempGuest = dao.getByopenID(guest);
        detrypt(tempGuest);
        return tempGuest;
    }

    public List<Guest> findListByOpenId(Guest guest) {
        List<Guest> listGuest = dao.findListByOpenId(guest);
        for (Guest temp : listGuest) {
            detrypt(temp);
        }
        return listGuest;
    }

	public Guest getByUnionID(Guest guest){
    	return guestdao.getByUnionID(guest);
	}


    @Transactional(readOnly = false)
    //pc端授权
    public void updategrantstatus(Guest guest) {
        if (guest != null && guest.getId() != null) {
            //设置为已授权
            guest.setGrantstatus(WeChatConstant.Weixintype.TYPE_ACCESS);
            guestdao.updategrantstatus(guest);
        }
    }

    /**
     * 加密参数
     *
     * @param guest
     * @return
     */
    private static void encrypt(Guest guest) {
//			if(guest != null) {
//				try {
//					guest.setGname(SecurityUtil.encrypt(guest.getGname()));
//					guest.setGidcardNo(SecurityUtil.encrypt(guest.getGidcardNo()));
//					guest.setGphone(SecurityUtil.encrypt(guest.getGphone()));
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
    }

    private static void detrypt(Guest guest) {
//			if (guest != null) {
//				try {
//					guest.setGname(SecurityUtil.detrypt(guest.getGname()));
//					guest.setGidcardNo(SecurityUtil.detrypt(guest.getGidcardNo()));
//					guest.setGphone(SecurityUtil.detrypt(guest.getGphone()));
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
    }

    @Transactional(readOnly = false)
    public void updateByopenId(Guest guest) {
        encrypt(guest);
        guestdao.updateByopenId(guest);
    }

}