package com.call.log.infinity.utils;

/**
 * Created by Yong on 2017/3/1.
 */

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 根据座机号码的区号查询其归属地。
 */
public class TelephoneAreaCodeUtil {
    static final String TAG = "TelephoneAreaCodeUtil";

    public static final HashMap<String, String> TELEPHONE_AREA_CODE = new HashMap<>();

    public static final ArrayList<String> THREE_DIGIT_AREA_CODE = new ArrayList<>();

    //带区号的电话号码最少是10位
    private static final int TELEPHONE_NUMBER_LENGTH_MIN = 10;

    //区号是3位或者4位
    private static final int MIN_CODE_LENGTH = 3;
    private static final int MAX_CODE_LENGTH = 4;

    static {
        THREE_DIGIT_AREA_CODE.add("010");
        THREE_DIGIT_AREA_CODE.add("020");
        THREE_DIGIT_AREA_CODE.add("021");
        THREE_DIGIT_AREA_CODE.add("022");
        THREE_DIGIT_AREA_CODE.add("023");
        THREE_DIGIT_AREA_CODE.add("024");
        THREE_DIGIT_AREA_CODE.add("025");
        THREE_DIGIT_AREA_CODE.add("026");
        THREE_DIGIT_AREA_CODE.add("027");
        THREE_DIGIT_AREA_CODE.add("028");
        THREE_DIGIT_AREA_CODE.add("029");
    }

    static {
        TELEPHONE_AREA_CODE.put("010", "北京");
        TELEPHONE_AREA_CODE.put("021", "上海");
        TELEPHONE_AREA_CODE.put("022", "天津");
        TELEPHONE_AREA_CODE.put("023", "重庆");

//        TELEPHONE_AREA_CODE.put("852", "香港");
//        TELEPHONE_AREA_CODE.put("853", "澳门");

        TELEPHONE_AREA_CODE.put("0550", "安徽 滁州");
        TELEPHONE_AREA_CODE.put("0551", "安徽 合肥");
        TELEPHONE_AREA_CODE.put("0552", "安徽 蚌埠");
        TELEPHONE_AREA_CODE.put("0553", "安徽 芜湖");
        TELEPHONE_AREA_CODE.put("0554", "安徽 淮南");
        TELEPHONE_AREA_CODE.put("0555", "安徽 马鞍山");
        TELEPHONE_AREA_CODE.put("0556", "安徽 安庆");
        TELEPHONE_AREA_CODE.put("0557", "安徽 宿州");
        TELEPHONE_AREA_CODE.put("0558", "安徽 阜阳");
        TELEPHONE_AREA_CODE.put("0559", "安徽 黄山");
        TELEPHONE_AREA_CODE.put("0561", "安徽 淮北");
        TELEPHONE_AREA_CODE.put("0562", "安徽 铜陵");
        TELEPHONE_AREA_CODE.put("0563", "安徽 宣城");
        TELEPHONE_AREA_CODE.put("0564", "安徽 六安");
        TELEPHONE_AREA_CODE.put("0565", "安徽 巢湖");
        TELEPHONE_AREA_CODE.put("0566", "安徽 贵池");

        TELEPHONE_AREA_CODE.put("0591", "福建 福州");
        TELEPHONE_AREA_CODE.put("0592", "福建 厦门");
        TELEPHONE_AREA_CODE.put("0593", "福建 宁德");
        TELEPHONE_AREA_CODE.put("0594", "福建 莆田");
        TELEPHONE_AREA_CODE.put("0595", "福建 泉州");
        TELEPHONE_AREA_CODE.put("0595", "福建 晋江");
        TELEPHONE_AREA_CODE.put("0596", "福建 漳州");
        TELEPHONE_AREA_CODE.put("0597", "福建 龙岩");
        TELEPHONE_AREA_CODE.put("0598", "福建 三明");
        TELEPHONE_AREA_CODE.put("0599", "福建 南平");

        TELEPHONE_AREA_CODE.put("0930", "甘肃 临夏");
        TELEPHONE_AREA_CODE.put("0931", "甘肃 兰州");
        TELEPHONE_AREA_CODE.put("0932", "甘肃 定西");
        TELEPHONE_AREA_CODE.put("0933", "甘肃 平凉");
        TELEPHONE_AREA_CODE.put("0934", "甘肃 西峰");
        TELEPHONE_AREA_CODE.put("0935", "甘肃 武威");
        TELEPHONE_AREA_CODE.put("0936", "甘肃 张掖");
        TELEPHONE_AREA_CODE.put("0937", "甘肃 酒泉");
        TELEPHONE_AREA_CODE.put("0938", "甘肃 天水");
        TELEPHONE_AREA_CODE.put("0941", "甘肃 甘南州");
        TELEPHONE_AREA_CODE.put("0943", "甘肃 白银");

        TELEPHONE_AREA_CODE.put("020", "广东 广州");
        TELEPHONE_AREA_CODE.put("0751", "广东 韶关");
        TELEPHONE_AREA_CODE.put("0752", "广东 惠州");
        TELEPHONE_AREA_CODE.put("0753", "广东 梅州");
        TELEPHONE_AREA_CODE.put("0754", "广东 汕头");
        TELEPHONE_AREA_CODE.put("0755", "广东 深圳");
        TELEPHONE_AREA_CODE.put("0756", "广东 珠海");
        TELEPHONE_AREA_CODE.put("0757", "广东 佛山");
        TELEPHONE_AREA_CODE.put("0758", "广东 肇庆");
        TELEPHONE_AREA_CODE.put("0759", "广东 湛江");
        TELEPHONE_AREA_CODE.put("0760", "广东 中山");
        TELEPHONE_AREA_CODE.put("0762", "广东 河源");
        TELEPHONE_AREA_CODE.put("0763", "广东 清远");
        TELEPHONE_AREA_CODE.put("0765", "广东 顺德");
        TELEPHONE_AREA_CODE.put("0766", "广东 云浮");
        TELEPHONE_AREA_CODE.put("0768", "广东 潮州");
        TELEPHONE_AREA_CODE.put("0769", "广东 东莞");
        TELEPHONE_AREA_CODE.put("0660", "广东 汕尾");
        TELEPHONE_AREA_CODE.put("0661", "广东 潮阳");
        TELEPHONE_AREA_CODE.put("0662", "广东 阳江");
        TELEPHONE_AREA_CODE.put("0663", "广东 揭西");

        TELEPHONE_AREA_CODE.put("0770", "广西 防城港");
        TELEPHONE_AREA_CODE.put("0771", "广西 南宁");
        TELEPHONE_AREA_CODE.put("0772", "广西 柳州");
        TELEPHONE_AREA_CODE.put("0773", "广西 桂林");
        TELEPHONE_AREA_CODE.put("0774", "广西 梧州");
        TELEPHONE_AREA_CODE.put("0775", "广西 玉林");
        TELEPHONE_AREA_CODE.put("0776", "广西 百色");
        TELEPHONE_AREA_CODE.put("0777", "广西 钦州");
        TELEPHONE_AREA_CODE.put("0778", "广西 河池");
        TELEPHONE_AREA_CODE.put("0779", "广西 北海");

        TELEPHONE_AREA_CODE.put("0851", "贵州 贵阳");
        TELEPHONE_AREA_CODE.put("0852", "贵州 遵义");
        TELEPHONE_AREA_CODE.put("0853", "贵州 安顺");
        TELEPHONE_AREA_CODE.put("0854", "贵州 都均");
        TELEPHONE_AREA_CODE.put("0855", "贵州 凯里");
        TELEPHONE_AREA_CODE.put("0856", "贵州 铜仁");
        TELEPHONE_AREA_CODE.put("0857", "贵州 毕节");
        TELEPHONE_AREA_CODE.put("0858", "贵州 六盘水");
        TELEPHONE_AREA_CODE.put("0859", "贵州 兴义");

        TELEPHONE_AREA_CODE.put("0890", "海南 儋州");
        TELEPHONE_AREA_CODE.put("0898", "海南 海口");
        TELEPHONE_AREA_CODE.put("0899", "海南 三亚");

        TELEPHONE_AREA_CODE.put("0310", "河北 邯郸");
        TELEPHONE_AREA_CODE.put("0311", "河北 石家庄");
        TELEPHONE_AREA_CODE.put("0312", "河北 保定");
        TELEPHONE_AREA_CODE.put("0313", "河北 张家口");
        TELEPHONE_AREA_CODE.put("0314", "河北 承德");
        TELEPHONE_AREA_CODE.put("0315", "河北 唐山");
        TELEPHONE_AREA_CODE.put("0316", "河北 廊坊");
        TELEPHONE_AREA_CODE.put("0317", "河北 沧州");
        TELEPHONE_AREA_CODE.put("0318", "河北 衡水");
        TELEPHONE_AREA_CODE.put("0319", "河北 邢台");
        TELEPHONE_AREA_CODE.put("0335", "河北 秦皇岛");

        TELEPHONE_AREA_CODE.put("0450", "黑龙江 阿城");
        TELEPHONE_AREA_CODE.put("0451", "黑龙江 哈尔滨");
        TELEPHONE_AREA_CODE.put("0452", "黑龙江 齐齐哈尔");
        TELEPHONE_AREA_CODE.put("0453", "黑龙江 牡丹江");
        TELEPHONE_AREA_CODE.put("0454", "黑龙江 佳木斯");
        TELEPHONE_AREA_CODE.put("0455", "黑龙江 绥化");
        TELEPHONE_AREA_CODE.put("0456", "黑龙江 黑河");
        TELEPHONE_AREA_CODE.put("0457", "黑龙江 加格达奇");
        TELEPHONE_AREA_CODE.put("0458", "黑龙江 伊春");
        TELEPHONE_AREA_CODE.put("0459", "黑龙江 大庆");

        TELEPHONE_AREA_CODE.put("0370", "河南 商丘");
        TELEPHONE_AREA_CODE.put("0371", "河南 郑州");
        TELEPHONE_AREA_CODE.put("0372", "河南 安阳");
        TELEPHONE_AREA_CODE.put("0373", "河南 新乡");
        TELEPHONE_AREA_CODE.put("0374", "河南 许昌");
        TELEPHONE_AREA_CODE.put("0375", "河南 平顶山");
        TELEPHONE_AREA_CODE.put("0376", "河南 信阳");
        TELEPHONE_AREA_CODE.put("0377", "河南 南阳");
        TELEPHONE_AREA_CODE.put("0378", "河南 开封");
        TELEPHONE_AREA_CODE.put("0379", "河南 洛阳");
        TELEPHONE_AREA_CODE.put("0391", "河南 焦作");
        TELEPHONE_AREA_CODE.put("0392", "河南 鹤壁");
        TELEPHONE_AREA_CODE.put("0393", "河南 濮阳");
        TELEPHONE_AREA_CODE.put("0394", "河南 周口");
        TELEPHONE_AREA_CODE.put("0395", "河南 漯河");
        TELEPHONE_AREA_CODE.put("0396", "河南 驻马店");
        TELEPHONE_AREA_CODE.put("0398", "河南 三门峡");

        TELEPHONE_AREA_CODE.put("027", "湖北 武汉");
        TELEPHONE_AREA_CODE.put("0710", "湖北 襄城");
        TELEPHONE_AREA_CODE.put("0711", "湖北 鄂州");
        TELEPHONE_AREA_CODE.put("0712", "湖北 孝感");
        TELEPHONE_AREA_CODE.put("0713", "湖北 黄州");
        TELEPHONE_AREA_CODE.put("0714", "湖北 黄石");
        TELEPHONE_AREA_CODE.put("0715", "湖北 咸宁");
        TELEPHONE_AREA_CODE.put("0716", "湖北 荆沙");
        TELEPHONE_AREA_CODE.put("0717", "湖北 宜昌");
        TELEPHONE_AREA_CODE.put("0718", "湖北 恩施");
        TELEPHONE_AREA_CODE.put("0719", "湖北 十堰");
        TELEPHONE_AREA_CODE.put("0722", "湖北 随枣");
        TELEPHONE_AREA_CODE.put("0724", "湖北 荆门");
        TELEPHONE_AREA_CODE.put("0728", "湖北 江汉");

        TELEPHONE_AREA_CODE.put("0730", "湖南 岳阳");
        TELEPHONE_AREA_CODE.put("0731", "湖南 长沙");
        TELEPHONE_AREA_CODE.put("0732", "湖南 湘潭");
        TELEPHONE_AREA_CODE.put("0733", "湖南 株州");
        TELEPHONE_AREA_CODE.put("0734", "湖南 衡阳");
        TELEPHONE_AREA_CODE.put("0735", "湖南 郴州");
        TELEPHONE_AREA_CODE.put("0736", "湖南 常德");
        TELEPHONE_AREA_CODE.put("0737", "湖南 益阳");
        TELEPHONE_AREA_CODE.put("0738", "湖南 娄底");
        TELEPHONE_AREA_CODE.put("0739", "湖南 邵阳");
        TELEPHONE_AREA_CODE.put("0743", "湖南 吉首");
        TELEPHONE_AREA_CODE.put("0744", "湖南 张家界");
        TELEPHONE_AREA_CODE.put("0745", "湖南 怀化");
        TELEPHONE_AREA_CODE.put("0746", "湖南 永州冷");

        TELEPHONE_AREA_CODE.put("0790", "江西 新余");
        TELEPHONE_AREA_CODE.put("0791", "江西 南昌");
        TELEPHONE_AREA_CODE.put("0792", "江西 九江");
        TELEPHONE_AREA_CODE.put("0793", "江西 上饶");
        TELEPHONE_AREA_CODE.put("0794", "江西 临川");
        TELEPHONE_AREA_CODE.put("0795", "江西 宜春");
        TELEPHONE_AREA_CODE.put("0796", "江西 吉安");
        TELEPHONE_AREA_CODE.put("0797", "江西 赣州");
        TELEPHONE_AREA_CODE.put("0798", "江西 景德镇");
        TELEPHONE_AREA_CODE.put("0799", "江西 萍乡");
        TELEPHONE_AREA_CODE.put("0701", "江西 鹰潭");

        TELEPHONE_AREA_CODE.put("025", "江苏 南京");
        TELEPHONE_AREA_CODE.put("0510", "江苏 无锡");
        TELEPHONE_AREA_CODE.put("0511", "江苏 镇江");
        TELEPHONE_AREA_CODE.put("0512", "江苏 苏州");
        TELEPHONE_AREA_CODE.put("0513", "江苏 南通");
        TELEPHONE_AREA_CODE.put("0514", "江苏 扬州");
        TELEPHONE_AREA_CODE.put("0515", "江苏 盐城");
        TELEPHONE_AREA_CODE.put("0516", "江苏 徐州");
        TELEPHONE_AREA_CODE.put("0517", "江苏 淮阴");
        TELEPHONE_AREA_CODE.put("0517", "江苏 淮安");
        TELEPHONE_AREA_CODE.put("0518", "江苏 连云港");
        TELEPHONE_AREA_CODE.put("0519", "江苏 常州");
        TELEPHONE_AREA_CODE.put("0523", "江苏 泰州");

        TELEPHONE_AREA_CODE.put("0431", "吉林 长春");
        TELEPHONE_AREA_CODE.put("0432", "吉林 吉林");
        TELEPHONE_AREA_CODE.put("0433", "吉林 延吉");
        TELEPHONE_AREA_CODE.put("0434", "吉林 四平");
        TELEPHONE_AREA_CODE.put("0435", "吉林 通化");
        TELEPHONE_AREA_CODE.put("0436", "吉林 白城");
        TELEPHONE_AREA_CODE.put("0437", "吉林 辽源");
        TELEPHONE_AREA_CODE.put("0438", "吉林 松原");
        TELEPHONE_AREA_CODE.put("0439", "吉林 浑江");
        TELEPHONE_AREA_CODE.put("0440", "吉林 珲春");

        TELEPHONE_AREA_CODE.put("024", "辽宁 沈阳");
        TELEPHONE_AREA_CODE.put("0410", "辽宁 铁岭");
        TELEPHONE_AREA_CODE.put("0411", "辽宁 大连");
        TELEPHONE_AREA_CODE.put("0412", "辽宁 鞍山");
        TELEPHONE_AREA_CODE.put("0413", "辽宁 抚顺");
        TELEPHONE_AREA_CODE.put("0414", "辽宁 本溪");
        TELEPHONE_AREA_CODE.put("0415", "辽宁 丹东");
        TELEPHONE_AREA_CODE.put("0416", "辽宁 锦州");
        TELEPHONE_AREA_CODE.put("0417", "辽宁 营口");
        TELEPHONE_AREA_CODE.put("0418", "辽宁 阜新");
        TELEPHONE_AREA_CODE.put("0419", "辽宁 辽阳");
        TELEPHONE_AREA_CODE.put("0421", "辽宁 朝阳");
        TELEPHONE_AREA_CODE.put("0427", "辽宁 盘锦");
        TELEPHONE_AREA_CODE.put("0429", "辽宁 葫芦岛");

        TELEPHONE_AREA_CODE.put("0470", "内蒙古 海拉尔");
        TELEPHONE_AREA_CODE.put("0471", "内蒙古 呼和浩特");
        TELEPHONE_AREA_CODE.put("0472", "内蒙古 包头");
        TELEPHONE_AREA_CODE.put("0473", "内蒙古 乌海");
        TELEPHONE_AREA_CODE.put("0474", "内蒙古 集宁");
        TELEPHONE_AREA_CODE.put("0475", "内蒙古 通辽");
        TELEPHONE_AREA_CODE.put("0476", "内蒙古 赤峰");
        TELEPHONE_AREA_CODE.put("0477", "内蒙古 东胜");
        TELEPHONE_AREA_CODE.put("0478", "内蒙古 临河");
        TELEPHONE_AREA_CODE.put("0479", "内蒙古 锡林浩特");
        TELEPHONE_AREA_CODE.put("0482", "内蒙古 乌兰浩特");
        TELEPHONE_AREA_CODE.put("0483", "内蒙古 阿拉善左旗");

        TELEPHONE_AREA_CODE.put("0951", "宁夏 银川");
        TELEPHONE_AREA_CODE.put("0952", "宁夏 石嘴山");
        TELEPHONE_AREA_CODE.put("0953", "宁夏 吴忠");
        TELEPHONE_AREA_CODE.put("0954", "宁夏 固原");

        TELEPHONE_AREA_CODE.put("0971", "青海 西宁");
        TELEPHONE_AREA_CODE.put("0972", "青海 海东");
        TELEPHONE_AREA_CODE.put("0973", "青海 同仁");
        TELEPHONE_AREA_CODE.put("0974", "青海 共和");
        TELEPHONE_AREA_CODE.put("0975", "青海 玛沁");
        TELEPHONE_AREA_CODE.put("0976", "青海 玉树");
        TELEPHONE_AREA_CODE.put("0977", "青海 德令哈");

        TELEPHONE_AREA_CODE.put("0530", "山东 菏泽");
        TELEPHONE_AREA_CODE.put("0531", "山东 济南");
        TELEPHONE_AREA_CODE.put("0532", "山东 青岛");
        TELEPHONE_AREA_CODE.put("0533", "山东 淄博");
        TELEPHONE_AREA_CODE.put("0534", "山东 德州");
        TELEPHONE_AREA_CODE.put("0535", "山东 烟台");
        TELEPHONE_AREA_CODE.put("0536", "山东 淮坊");
        TELEPHONE_AREA_CODE.put("0537", "山东 济宁");
        TELEPHONE_AREA_CODE.put("0538", "山东 泰安");
        TELEPHONE_AREA_CODE.put("0539", "山东 临沂");

        TELEPHONE_AREA_CODE.put("0350", "山西 忻州");
        TELEPHONE_AREA_CODE.put("0351", "山西 太原");
        TELEPHONE_AREA_CODE.put("0352", "山西 大同");
        TELEPHONE_AREA_CODE.put("0353", "山西 阳泉");
        TELEPHONE_AREA_CODE.put("0354", "山西 榆次");
        TELEPHONE_AREA_CODE.put("0355", "山西 长治");
        TELEPHONE_AREA_CODE.put("0356", "山西 晋城");
        TELEPHONE_AREA_CODE.put("0357", "山西 临汾");
        TELEPHONE_AREA_CODE.put("0358", "山西 离石");
        TELEPHONE_AREA_CODE.put("0359", "山西 运城");

        TELEPHONE_AREA_CODE.put("029", "陕西 西安");
        TELEPHONE_AREA_CODE.put("0910", "陕西 咸阳");
        TELEPHONE_AREA_CODE.put("0911", "陕西 延安");
        TELEPHONE_AREA_CODE.put("0912", "陕西 榆林");
        TELEPHONE_AREA_CODE.put("0913", "陕西 渭南");
        TELEPHONE_AREA_CODE.put("0914", "陕西 商洛");
        TELEPHONE_AREA_CODE.put("0915", "陕西 安康");
        TELEPHONE_AREA_CODE.put("0916", "陕西 汉中");
        TELEPHONE_AREA_CODE.put("0917", "陕西 宝鸡");
        TELEPHONE_AREA_CODE.put("0919", "陕西 铜川");

        TELEPHONE_AREA_CODE.put("028", "四川 成都");
        TELEPHONE_AREA_CODE.put("0810", "四川 涪陵");
        TELEPHONE_AREA_CODE.put("0811", "四川 重庆");
        TELEPHONE_AREA_CODE.put("0812", "四川 攀枝花");
        TELEPHONE_AREA_CODE.put("0813", "四川 自贡");
        TELEPHONE_AREA_CODE.put("0814", "四川 永川");
        TELEPHONE_AREA_CODE.put("0816", "四川 绵阳");
        TELEPHONE_AREA_CODE.put("0817", "四川 南充");
        TELEPHONE_AREA_CODE.put("0818", "四川 达县");
        TELEPHONE_AREA_CODE.put("0819", "四川 万县");
        TELEPHONE_AREA_CODE.put("0825", "四川 遂宁");
        TELEPHONE_AREA_CODE.put("0826", "四川 广安");
        TELEPHONE_AREA_CODE.put("0827", "四川 巴中");
        TELEPHONE_AREA_CODE.put("0830", "四川 泸州");
        TELEPHONE_AREA_CODE.put("0831", "四川 宜宾");
        TELEPHONE_AREA_CODE.put("0832", "四川 内江");
        TELEPHONE_AREA_CODE.put("0833", "四川 乐山");
        TELEPHONE_AREA_CODE.put("0834", "四川 西昌");
        TELEPHONE_AREA_CODE.put("0835", "四川 雅安");
        TELEPHONE_AREA_CODE.put("0836", "四川 康定");
        TELEPHONE_AREA_CODE.put("0837", "四川 马尔康");
        TELEPHONE_AREA_CODE.put("0838", "四川 德阳");
        TELEPHONE_AREA_CODE.put("0839", "四川 广元");
        TELEPHONE_AREA_CODE.put("0840", "四川 泸州");

        TELEPHONE_AREA_CODE.put("0891", "西藏 拉萨");
        TELEPHONE_AREA_CODE.put("0892", "西藏 日喀则");
        TELEPHONE_AREA_CODE.put("0893", "西藏 山南");
        TELEPHONE_AREA_CODE.put("0894", "西藏 林芝");
        TELEPHONE_AREA_CODE.put("0895", "西藏 昌都");
        TELEPHONE_AREA_CODE.put("0896", "西藏 那曲");

        TELEPHONE_AREA_CODE.put("0870", "云南 昭通");
        TELEPHONE_AREA_CODE.put("0871", "云南 昆明");
        TELEPHONE_AREA_CODE.put("0872", "云南 大理");
        TELEPHONE_AREA_CODE.put("0873", "云南 个旧");
        TELEPHONE_AREA_CODE.put("0874", "云南 曲靖");
        TELEPHONE_AREA_CODE.put("0875", "云南 保山");
        TELEPHONE_AREA_CODE.put("0876", "云南 文山");
        TELEPHONE_AREA_CODE.put("0877", "云南 玉溪");
        TELEPHONE_AREA_CODE.put("0878", "云南 楚雄");
        TELEPHONE_AREA_CODE.put("0879", "云南 思茅");
        TELEPHONE_AREA_CODE.put("0691", "云南 景洪");
        TELEPHONE_AREA_CODE.put("0692", "云南 潞西");
        TELEPHONE_AREA_CODE.put("0881", "云南 东川");
        TELEPHONE_AREA_CODE.put("0883", "云南 临沧");
        TELEPHONE_AREA_CODE.put("0886", "云南 六库");
        TELEPHONE_AREA_CODE.put("0887", "云南 中甸");
        TELEPHONE_AREA_CODE.put("0888", "云南 丽江");

        TELEPHONE_AREA_CODE.put("0570", "浙江 衢州");
        TELEPHONE_AREA_CODE.put("0571", "浙江 杭州");
        TELEPHONE_AREA_CODE.put("0572", "浙江 湖州");
        TELEPHONE_AREA_CODE.put("0573", "浙江 嘉兴");
        TELEPHONE_AREA_CODE.put("0574", "浙江 宁波");
        TELEPHONE_AREA_CODE.put("0575", "浙江 绍兴");
        TELEPHONE_AREA_CODE.put("0576", "浙江 台州");
        TELEPHONE_AREA_CODE.put("0577", "浙江 温州");
        TELEPHONE_AREA_CODE.put("0578", "浙江 丽水");
        TELEPHONE_AREA_CODE.put("0579", "浙江 金华");
        TELEPHONE_AREA_CODE.put("0580", "浙江 舟山");

        TELEPHONE_AREA_CODE.put("0991", "新疆 乌鲁木齐");
        TELEPHONE_AREA_CODE.put("0902", "新疆 哈密");
        TELEPHONE_AREA_CODE.put("0990", "新疆 克拉玛依");
        TELEPHONE_AREA_CODE.put("0903", "新疆 和田");
        TELEPHONE_AREA_CODE.put("0993", "新疆 石河子");
        TELEPHONE_AREA_CODE.put("0997", "新疆 阿克苏");
        TELEPHONE_AREA_CODE.put("0999", "新疆 伊宁");
        TELEPHONE_AREA_CODE.put("0909", "新疆 博乐");
        TELEPHONE_AREA_CODE.put("0908", "新疆 阿图什");
        TELEPHONE_AREA_CODE.put("0995", "新疆 吐鲁番");
        TELEPHONE_AREA_CODE.put("0901", "新疆 塔城");
        TELEPHONE_AREA_CODE.put("0994", "新疆 昌吉");
        TELEPHONE_AREA_CODE.put("0996", "新疆 库尔勒");
        TELEPHONE_AREA_CODE.put("0906", "新疆 阿勒泰");
        TELEPHONE_AREA_CODE.put("0992", "新疆 奎屯");
        TELEPHONE_AREA_CODE.put("0998", "新疆 喀什");
    }

    public static String getTelephoneAreaByCode(String code) {
        return TELEPHONE_AREA_CODE.get(code);
    }

    public static String getTelephoneAreaByPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return null;
        }

        if (phoneNumber.length() >= TELEPHONE_NUMBER_LENGTH_MIN) {
            String result = getTelephoneAreaByCode(phoneNumber.substring(0, MIN_CODE_LENGTH));

            if (TextUtils.isEmpty(result)) {
                result = getTelephoneAreaByCode(phoneNumber.substring(0, MAX_CODE_LENGTH));
            }

            return result;
        }

        return null;
    }
}
