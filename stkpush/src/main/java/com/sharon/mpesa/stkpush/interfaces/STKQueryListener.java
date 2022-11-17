package com.sharon.mpesa.stkpush.interfaces;

import com.sharon.mpesa.stkpush.api.response.STKPushResponse;

/**
 * @author Fredrick Ochieng on 02/02/2018.
 */

public interface STKQueryListener {

    void onResponse(STKPushResponse stkPushResponse);

    void onError(Throwable throwable);
}
