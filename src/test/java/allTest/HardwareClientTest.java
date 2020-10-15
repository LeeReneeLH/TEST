package allTest;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.coffer.external.hessian.IHardwareService;

public class HardwareClientTest {
	public static void main(String[] args) {
		// HessianProxyFactory<IHardwareService> hessianProxyFactory = new
		// HessianProxyFactory<IHardwareService>(
		// IHardwareService.class);
		// IHardwareService s = hessianProxyFactory.create("172.16.3.63", 18888,
		// null);
		// System.out.println(s.test("Ray"));
		String url = "http://127.0.0.1:8070/frame/hardwareService";
//		String url = "http://172.16.3.242:8089/frame/hardwareService";
		HessianProxyFactory factory = new HessianProxyFactory();
		IHardwareService s;
		try {
			s = (IHardwareService) factory.create(IHardwareService.class, url);
			String resultJson = "";
			
//			// 接口21
//			System.out.println(s.service("{"
//					+ "'serialorderNo':'289000160105100005',"
//					+ "'boxList':["
//					+ "		{'boxNo':'00014544','outDate':null},"
//					+ "		{'boxNo':'00014512','outDate':null},"
//					+ "		{'boxNo':'00106711','outDate':null},"
//					+ "		{'boxNo':'00013412','outDate':null},"
//					+ "		{'boxNo':'00017812','outDate':null},"
//					+ "		{'boxNo':'00016712','outDate':null}],"
//					+ "'handoverList':null,"
//					+ "'acceptList':null,"
//					+ "'managerList':["
//					+ "		{'id':'48f87b5333534d58b860ea4e618fb808','name':'樊贺华','reason':'0'},"
//					+ "		{'id':'48f87b5333534d58b860ea4e618fb808','name':'樊贺华','reason':'0'},"
//					+ "		{'id':'48f87b5333534d58b860ea4e618fb808','name':'樊贺华','reason':'0'},"
//					+ "		{'id':'48f87b5333534d58b860ea4e618fb808','name':'樊贺华','reason':'0'}],"
//					+ "'userId':'48f87b5333534d58b860ea4e618fb808',"
//					+ "'userName':'樊贺华',"
//					+ "'versionNo':'01',"
//					+ "'serviceNo':'21',"
//					+ "'officeId':'28900032',"
//					+ "'pdaCode':'10-03-A8-05-DB-0C-21-49'}"));
			// 接口 04 : 人员身份验证
//			resultJson = s.service("{'versionNo':'01','serviceNo':'04','idcardNo':'410311196702100516'}");
			//接口 08 : 人员身份
//			resultJson = s.service("{'versionNo':'01','serviceNo':'08','officeId':'28900145','pdaCode':'17-16-40-05-DB-0C-21-49'}");
			
//			resultJson = s.service(
//					"{'boxList':["
//					+ "		{'boxNo':'00106711','outDate':''},"				// 滨江
//					+ "		{'boxNo':'00013412','outDate':'20160107'},"	    // 滨江
//					+ "		{'boxNo':'00014512','outDate':'20160107'}],"	// 滨江
//					+ "'acceptList':null,"
//					+ "'handoverList':null,"
//					+ "'managerList':["
//					+ "		{'id':'48f87b5333534d58b860ea4e618fb808','name':'樊贺华','reason':'0'}],"
//					+ "'userId':'48f87b5333534d58b860ea4e618fb808',"
//					+ "'userName':'樊贺华',"
//					+ "'versionNo':'01',"
//					+ "'serviceNo':'19',"
//					+ "'officeId':'28900032',"
//					+ "'pdaCode':'10-03-A8-05-DB-0C-21-49'}");

			// 接口18：金库下拨登记
//			resultJson = s.service("{'boxList':[{'boxNo':'00088611','outDate':null},{'boxNo':'00048212','outDate':null},{'boxNo':'00025712','outDate':null},{'boxNo':'00110212','outDate':null},{'boxNo':'00021312','outDate':null},{'boxNo':'00016712','outDate':null},{'boxNo':'00111312','outDate':null},{'boxNo':'00011212','outDate':null},{'boxNo':'00001111','outDate':null},{'boxNo':'00116811','outDate':null},{'boxNo':'00010112','outDate':null},{'boxNo':'00017812','outDate':null},{'boxNo':'00106711','outDate':null},{'boxNo':'00112412','outDate':null},{'boxNo':'00111311','outDate':null},{'boxNo':'00117911','outDate':null},{'boxNo':'00103411','outDate':null},{'boxNo':'00115711','outDate':null},{'boxNo':'00023512','outDate':null},{'boxNo':'00024612','outDate':null},{'boxNo':'00020212','outDate':null},{'boxNo':'00096511','outDate':null},{'boxNo':'00100111','outDate':null},{'boxNo':'00047112','outDate':null},{'boxNo':'00018912','outDate':null}],'userId':'cc52038077d84237bea3864f7302748f','userName':'张卿','versionNo':'01','serviceNo':'18','officeId':'28900000','pdaCode':'14-21-08-05-DB-0C-21-49'}");
			
			
//			// 接口2
//			resultJson = s.service("{'resultFlag': '00', 'versionNo': '01', "
//					+ "'list': [{"
//					+ "		'boxNo': '00041512', "
//					+ "		'boxType': '12', "
//					+ "		'rfid': '2890007800041512', "
//					+ "		'officeName': '群星支行', "
//					+ "		'boxStatusName': '空箱', "
//					+ "		'boxTypeName': "
//					+ "		'尾箱', "
//					+ "		'delFlag': '0'} ], "
//					+ "'serviceNo': '02'}");
			
			// 接口34：线路管理
//			resultJson = s.service("{'versionNo':'01','serviceNo':'34','routeId':'20160113105847814','escortId1':'20160112145924386','escortId2':'20160112150047628'} ");
			
			// 接口40：同步押运人员信息
//			resultJson = s.service("{'versionNo':'01','serviceNo':'40','userType':'90','userCode':null,'officeId':null,'pdaCode':null}");

			// 接口09：同步押运人员信息
//			resultJson = s.service("{'userType':'90','userCode':'1962','versionNo':'01','serviceNo':'09','officeId':'28900000','pdaCode':'14-21-08-05-DB-0C-21-49'}");

            // 接口50：RFID面值查询
//            resultJson = s.service("{'officeId':'28900145','inoutType':'1','rfidList':['30A197C7AC1F3841'],'versionNo':'01','serviceNo':'50'} ");
			
            // 接口49：RFID面值绑定
//            resultJson = s.service("{'officeId':'28900167','denomination':'0.5','rfidList':['3638307BAC1F3841'],'reBindingFlag':'0','userId':'000001','userName':'测试用户','interfaceName':null,'versionNo':'01','serviceNo':'49'}");

//            // 接口44：人行库外入库确认接口
//			resultJson = s.service("{'officeId':'28900145','versionNo':'01','serialorderNo':'000001160802560001', 'serviceNo':'44',"
//					+ "'boxList':[{'goodsId':'101085103110','goodsNum':'1', 'rfid':'200001'},"
//					+ "{'goodsId':'101085103110','goodsNum':'1', 'rfid':'200002'},"
//					+ "{'goodsId':'101085103110','goodsNum':'1', 'rfid':'200003'},"
//					+ "{'goodsId':'101085103110','goodsNum':'1', 'rfid':'200004'},"
//					+ "{'goodsId':'101085103110','goodsNum':'1', 'rfid':'200005'},"
//					+ "{'goodsId':'101085103110','goodsNum':'1', 'rfid':'200006'},"
//					+ "{'goodsId':'101085103110','goodsNum':'1', 'rfid':'200007'},"
//					+ "{'goodsId':'101085103110','goodsNum':'1', 'rfid':'200008'},"
//					+ "{'goodsId':'101085103110','goodsNum':'1', 'rfid':'200009'},"
//					+ "{'goodsId':'101085103110','goodsNum':'1', 'rfid':'200010'}"
//					+ "],'managerList':null,'userId':'7a20a5ae706a423cac2b0189ce69db01','userName':'吴宇航'}");
			// 接口42：人行库外出入库查询接口
//			resultJson = s.service("{'officeId':'28900145','versionNo':'01','serviceNo':'42', 'inoutType':'0','list':[{'businessType':'51'}]}");
//			resultJson = s.service("{'officeId':'28900145','inoutType':'1','list':[{'businessType':'56'}],'versionNo':'01','serviceNo':'42'}");
            // 接口43：人行库外出库确认接口
//			resultJson = s.service("{'officeId':'28900145','serialorderNo':'000001160803510002',"
//					+ "'boxList':[{'rfid':'20','goodsId':'101065102110','goodsNum':'1'}"
//					
//					+ "],'managerList':null,'outFlag':'0','userId':'7a20a5ae706a423cac2b0189ce69db01','userName':'吴宇航','versionNo':'01','serviceNo':'43'}");
            // 接口45：人行入库交接
//            resultJson = s.service("{'inoutType':'1','serialorderNo':'000001160713530001','handoverList':[{'id':'00','name':'罗立峰','handType':'1','type':'1','officeName':'第一'}],'acceptList':[{'id':'01','name':'李东','handType':'1','type':null,'officeName':null},{'id':'02','name':'王林','handType':'1','type':null,'officeName':null},{'id':'03','name':'阎得水','handType':'1','type':null,'officeName':null},{'id':'04','name':'孙士博','handType':'1','type':null,'officeName':null}],'managerList':null,'userId':'lyrhczy','userName':'洛人行操作员','versionNo':'01','serviceNo':'45'}");
			 // 接口45：人行出库交接
//			resultJson = s.service("{'inoutType':'0','serialorderNo':'000001160802560001','acceptList':[{'id':'00','name':'罗立峰','handType':'1','type':'1','officeName':'第一'}],'handoverList':[{'id':'01','name':'李东','handType':'1','type':null,'officeName':null},{'id':'02','name':'王林','handType':'1','type':null,'officeName':null},{'id':'03','name':'阎得水','handType':'1','type':null,'officeName':null},{'id':'04','name':'孙士博','handType':'1','type':null,'officeName':null}],'managerList':null,'userId':'lyrhczy','userName':'洛人行操作员','versionNo':'01','serviceNo':'45'}");
            // 接口46：人行库区变更
//          resultJson = s.service("" 
//                  + "{'officeId':'28900145'," 
//                  + " 'inoutType':'9'," 
//                  + " 'boxList':"
//                  + "     [{'rfid':'3637DD09AC1F3841', 'areaId':'123456'}," 
//                  + "      {'rfid':'36383D56AC1F3841', 'areaId':'123456'},"
//                  + "      ],"
//                  + " 'userId':'z11',"
//                  + " 'userName':'测试用户'," 
//                  + " 'versionNo':'01'," 
//                  + " 'serviceNo':'46'}");
			
            // 接口47：人行同步库区信息
//          resultJson = s.service("{'officeId':'28900145','versionNo':'01','serviceNo':'47'}");
	         // 接口55：人行同步库区信息
//	         resultJson = s.service("{'officeId':'28900145','versionNo':'01','serviceNo':'55'}");
	      // 接口53：原封新券回收接口
//	         resultJson = s.service("{'officeId':'','versionNo':'01','serviceNo':'53', 'list':[{'boxNo':'333333333'}] ,'userId':'4a539c62c00b4f87b45af3b8077be0b8','userName':'洛人行操作员'}");
	         
			// 接口56：原封新券入库接口(new)
//			resultJson = s.service("{'officeId':'28900145','versionNo':'01', 'serviceNo':'56','list':[{'goodsId':'101095101114','boxNo':'16', 'originalTranslate':'人民币上的号码在特殊'}],'userId':'4a539c62c00b4f87b45af3b8077be0b8','userName':'洛人行操作员'}");
			
//			resultJson = s.service("{'idcardNo':'210211197909023150','versionNo':'01','serviceNo':'04'}");
			// 接口13：同步数据字典信息（盘点）
          // resultJson = s.service("{'officeId':'28900145','versionNo':'01','serviceNo':'13'}");
			// 接口57：同步数据字典信息（盘点）
//			resultJson = s.service("{'officeId':'28900145','versionNo':'01','serviceNo':'57','goodsId':'101065103101','areaId':'d9990a88b1104186ae8400c152d3afe0', "
//					+ "'boxList':[{'rfid':'2222222BAC1F3844'},{'rfid':'00000001BAC1F3844'},"
//					+ "{'rfid':'00000011BAC1F3844'},{'rfid':'00000012BAC1F3844'},{'rfid':'00000013BAC1F3844'},"
//					+ "{'rfid':'00000014BAC1F3844'},{'rfid':'00000015BAC1F3844'},{'rfid':'00000016BAC1F3844'},"
//					+ "{'rfid':'00000017BAC1F3844'},{'rfid':'00000018BAC1F3844'},{'rfid':'00000019BAC1F3844'}]"
//					+ ",'userId':'4a539c62c00b4f87b45af3b8077be0b8','userName':'洛人行操作员'}");
//			resultJson = s.service("{'goodsId':'101065101110','areaId':'5049576d1fa74d519418c4cd8aa0f333','boxList':[{'rfid':'33EB0123AC1F3841'},{'rfid':'2AE360C2AC1F3841'},{'rfid':'33EB5BBDAC1F3841'},{'rfid':'33EAFB21AC1F3841'}],'userId':'7a20a5ae706a423cac2b0189ce69db01','userName':'吴宇航','versionNo':'01','serviceNo':'57','officeId':'28900145','pdaCode':'U8S2W3G1'}");
			
//			resultJson = s.service("{'officeId':'28900145','versionNo':'01','serialorderNo':'000001160718500003', 'serviceNo':'44',"
//			+ "'boxList':[{'goodsId':'101065101110','goodsNum':'1', 'rfid':'121212121'}"
//			+ "]}");
//			
//			resultJson = s.service("{'officeId':'28900145','versionNo':'01', 'serviceNo':'56',"
//					+ "'list':[{'goodsId':'101095101114','boxNo':'16', 'originalTranslate':'人民币上的号码在特殊'}],"
//					+ "'userId':'4a539c62c00b4f87b45af3b8077be0b8','userName':'洛人行操作员'}");
//			
//			String[] goodsIdArray = {
//					"101095105114"};
//			
//			StringBuffer buffer = new StringBuffer();
//			buffer.append("{'officeId':'28900145','versionNo':'01',"
//					+ " 'serviceNo':'56','userId':'7a20a5ae706a423cac2b0189ce69db01','userName':'吴宇航',");
//			buffer.append("'list':[");
//			for (int iIndex = 0; iIndex < 1; iIndex ++) {
//				
//				for (int jIndex = 0; jIndex < 132; jIndex ++) {
//					buffer.append("{'goodsId':'" + goodsIdArray[iIndex] + "',");
//					buffer.append("'goodsNum':'1', ");
//					buffer.append("'originalTranslate':'人民币上的号码在特殊"+ Integer.toString(iIndex) + Integer.toString(jIndex) +"',");
//					buffer.append("'boxNo':'" + 200 + Integer.toString(iIndex) + Integer.toString(jIndex) + "'},");
//				}
//				
//			}
//			String string = buffer.toString();
//			string = string.substring(0, string.length() - 1) + "]}";
//			resultJson = s.service(string);
//			System.out.println(resultJson);
			
			
			// 接口58
//          resultJson = s.service("{'officeId':'28900145','serialorderNo':'000001160824560001','versionNo':'01','serviceNo':'58'}");
////			//  接口60  入库
//			resultJson = s.service("{'officeId':'28900145','versionNo':'01','serialorderNo':'000001160824560001', 'serviceNo':'60','inoutType':'1',"
//					+ "'boxList':[{'goodsId':'101085107110','goodsNum':'1', 'rfid':'33B01527E2010F20'},"
//					+ "{'goodsId':'101085107110','goodsNum':'1', 'rfid':'33B10583AC1F3841'}"
//					
//					+ "],'handoverList':[{'id':'00','name':'罗立峰','handType':'1','type':'1','officeName':'第一'}],"
//					+ "'acceptList':[{'id':'01','name':'李东','handType':'1','type':null,'officeName':null},"
//					+ "{'id':'02','name':'王林','handType':'1','type':null,'officeName':null},"
//					+ "{'id':'03','name':'阎得水','handType':'1','type':null,'officeName':null},"
//					+ "{'id':'04','name':'孙士博','handType':'1','type':null,'officeName':null}],"
//					+ "'managerList':null,'userId':'b97063aec1584b93ac6b07abb62b55b3','userName':'王冬'}");
			//  接口59出库
//			resultJson = s.service("{'officeId':'28900145','versionNo':'01','serialorderNo':'000001160824550001', 'serviceNo':'59','outFlag':'0','inoutType':'0',"
//					+ "'boxList':[{'goodsId':'101085107110','goodsNum':'1', 'rfid':'33B01527E2010F20'},"
//					+ "{'goodsId':'101085107110','goodsNum':'1', 'rfid':'33B10583AC1F3841'}"
//					
//					+ "],'handoverList':[{'id':'00','name':'罗立峰','handType':'1','type':'1','officeName':'第一'}],"
//					+ "'acceptList':[{'id':'01','name':'李东','handType':'1','type':null,'officeName':null},"
//					+ "{'id':'02','name':'王林','handType':'1','type':null,'officeName':null},"
//					+ "{'id':'03','name':'阎得水','handType':'1','type':null,'officeName':null},"
//					+ "{'id':'04','name':'孙士博','handType':'1','type':null,'officeName':null}],"
//					+ "'managerList':null,'userId':'b97063aec1584b93ac6b07abb62b55b3','userName':'王冬'}");
			
			//接口 68 : 人员身份
//			resultJson = s.service("{'versionNo':'01','serviceNo':'68','officeId':'28900145'}");
//			// 接口19：网点上缴登记
			
			//接口 69 : 人员身份
			resultJson = s.service("{'versionNo':'01','serviceNo':'69','officeId':'28900145'}");
			
//			// 接口19：网点上缴登记
			System.out.println(resultJson);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}
}
