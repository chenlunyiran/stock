package com.twotiger.stock.util.httpclient;

/**
 * Created by lei on 2018/11/22.
 */
public enum ThirdTxCode {
    效验("/zzg/verify"),
    资产查询("/zzg/exAssetQuery"),
    资产兑换("/zzg/exAssetExchange"),
    查询合同("/zzg/exchangeContract");

    private String url;

    private ThirdTxCode(String url) {
        this.url = url;
    }

    public String url() {
        return this.url;
    }

}
