package org.nebulositytech;

public enum PartnerProcessingState {
  SUCCESS,
  FAILURE,

  HARD_FAILURE,

  RETRIABLE_FAILURE,
  RETRY
}
