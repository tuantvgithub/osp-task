package co.osp.base.businessservice.constant.enumeration;

import co.osp.base.businessservice.entity.KeyValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityLabel{

    public static final List<KeyValue> list()
    {
        List<KeyValue> list = new ArrayList<>();

        list.add(new KeyValue().key("co.osp.base.businessservice.entity.Company").value("Doanh nghiệp"));
        list.add(new KeyValue().key("co.osp.base.businessservice.entity.LicCpn").value("Giấy phép viễn thỗng"));
        list.add(new KeyValue().key("co.osp.base.businessservice.entity.RevokeLicCpn").value("Thu hồi giấy phép viễn thông"));
        list.add(new KeyValue().key("co.osp.base.businessservice.entity.TlcCodeType").value("Loại mã số"));
        list.add(new KeyValue().key("co.osp.base.businessservice.entity.TlcCpnCode").value("Tổng hợp hiện trạng kho số"));
        list.add(new KeyValue().key("co.osp.base.businessservice.entity.DecisionHis").value("Biến động mã số"));
        list.add(new KeyValue().key("co.osp.base.businessservice.entity.TlcCodeFee").value("Đối tượng chịu phí"));
        list.add(new KeyValue().key("co.osp.base.businessservice.entity.TlcCode").value("Mã số"));
        list.add(new KeyValue().key("co.osp.base.domain.RptCpn").value("Báo cáo kỳ"));
        list.add(new KeyValue().key("co.osp.base.domain.VT01").value("VT01"));
        list.add(new KeyValue().key("co.osp.base.domain.VT02").value("VT02"));
        list.add(new KeyValue().key("co.osp.base.domain.VT03").value("VT03"));
        list.add(new KeyValue().key("co.osp.base.domain.VT04").value("VT04"));
        list.add(new KeyValue().key("co.osp.base.domain.VT05Domestic").value("VT05 Kết nối Internet trong nước"));
        list.add(new KeyValue().key("co.osp.base.domain.VT05International").value("VT05 Kết nối Internet quốc tế"));
        list.add(new KeyValue().key("co.osp.base.domain.VT05Station").value("VT05 Tổng vị trí nhà trạm thu phát sóng di động"));
        list.add(new KeyValue().key("co.osp.base.domain.VT05StationCp").value("VT05 Số lượng vị trí nhà trạm chia sẻ, sử dụng chung"));
        list.add(new KeyValue().key("co.osp.base.domain.VT05Synthetic").value("VT05 Tổng hợp cả nước dung lượng kết nối internet quốc tế/trong nước"));
        list.add(new KeyValue().key("co.osp.base.domain.VT06Infra").value("VT06 Số liệu hạ tầng theo địa bàn tỉnh/thành phố"));
        list.add(new KeyValue().key("co.osp.base.domain.VT06Human").value("VT06 Nhân lực, hạ tầng viễn thông"));
        list.add(new KeyValue().key("co.osp.base.domain.VT07").value("VT07"));
        list.add(new KeyValue().key("co.osp.base.domain.VT08").value("VT08"));
        list.add(new KeyValue().key("co.osp.base.domain.VT09").value("VT09"));
        list.add(new KeyValue().key("co.osp.base.domain.VT10").value("VT10"));
        list.add(new KeyValue().key("co.osp.base.domain.Inspect06").value("Báo cáo điều tra mức sống dân cư"));

        return list;
    }

    public static final Map<String, String> map()
    {
        List<KeyValue> list = list();
        Map<String, String> map = new HashMap<>();
        for(KeyValue k : list){
            map.put(k.getKey(), k.getValue());
        }

        return map;
    }

}
