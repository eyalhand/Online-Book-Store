package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import java.io.Serializable;

/**
 * A broadcast which is sent only once by TimeService. It notifies the other services that it is time to be terminated
 */
public class TerminateBroadcast implements Broadcast {}
