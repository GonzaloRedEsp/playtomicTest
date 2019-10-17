package com.playtomic.tests.wallet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class WalletResponse implements Serializable {

    private String httpCode;
    private String errorMessage;
}
