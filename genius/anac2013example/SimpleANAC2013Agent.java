package anac2013example;

import java.io.Serializable;

import negotiator.Agent;
import negotiator.Bid;
import negotiator.NegotiationResult;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;

/**
 * This agent is an example of how to create an ANAC2013 agent which learns
 * during the tournament. This agent is a variant of the random agent.
 * 
 * @author M. Hendrikx
 */
public class SimpleANAC2013Agent extends Agent {

	/** The minimum utility a bid should have to be accepted or offered. */
	private double MINIMUM_BID_UTILITY;
	/** The opponent's last action. */
	private Bid opponentLastBid;
	/** Bid with the highest possible utility. */
	private Bid maxBid;

	public SimpleANAC2013Agent() {
	}

	/**
	 * Initialize the target utility to MAX(rv, max). Where rv is the
	 * reservation value of the preference profile and max is the highest
	 * utility received on the current preference profile.
	 */
	public void init() {
		Serializable prev = this.loadSessionData();
		if (prev != null) {
			double previousOutcome = (Double) prev;
			MINIMUM_BID_UTILITY = Math.max(Math.max(
					utilitySpace.getReservationValueUndiscounted(),
					previousOutcome), 0.5);
		} else {
			MINIMUM_BID_UTILITY = utilitySpace
					.getReservationValueUndiscounted();
		}
		System.out.println("Minimum bid utility: " + MINIMUM_BID_UTILITY);
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getName() {
		return "Simple ANAC2013 Agent";
	}

	/**
	 * Set the target utility for the next match on the same preference profile.
	 * If the received utility is higher than the current target, save the
	 * received utility as the new target utility.
	 */
	public void endSession(NegotiationResult result) {
		if (result.getMyDiscountedUtility() > MINIMUM_BID_UTILITY) {
			saveSessionData(new Double(result.getMyDiscountedUtility()));
		}
		System.out.println(result);
	}

	/**
	 * Retrieve the bid from the opponent's last action.
	 */
	public void ReceiveMessage(Action opponentAction) {
		if (opponentAction instanceof Offer) {
			opponentLastBid = ((Offer) opponentAction).getBid();
		}
	}

	/**
	 * Accept if the utility of the opponent's is higher than the target
	 * utility; else return a random bid with a utility at least equal to the
	 * target utility.
	 */
	public Action chooseAction() {
		if (opponentLastBid != null
				&& getUtility(opponentLastBid) >= MINIMUM_BID_UTILITY) {
			return new Accept(getAgentID(), opponentLastBid);
		}
		return getRandomBid(MINIMUM_BID_UTILITY);
	}

	/**
	 * Return a bid with a utility at least equal to the target utility, or the
	 * bid with the highest utility possible if it takes too long to find.
	 * 
	 * @param target
	 * @return found bid.
	 */
	private Action getRandomBid(double target) {
		Bid bid = null;
		try {
			int loops = 0;
			do {
				bid = utilitySpace.getDomain().getRandomBid(null);
				loops++;
			} while (loops < 100000 && utilitySpace.getUtility(bid) < target);
			if (bid == null) {
				if (maxBid == null) {
					// this is a computationally expensive operation, therefore
					// cache result
					maxBid = utilitySpace.getMaxUtilityBid();
				}
				bid = maxBid;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Offer(getAgentID(), bid);
	}
}