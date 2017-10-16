package storageexample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import list.Tuple;
import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.parties.NegotiationInfo;
import negotiator.persistent.PersistentDataContainer;
import negotiator.persistent.PersistentDataType;
import negotiator.persistent.StandardInfo;
import negotiator.persistent.StandardInfoList;
import negotiator.timeline.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;

/**
 * Sample party that accepts the Nth offer, where N is the number of sessions
 * this [agent-profile] already did.
 */
public class GroupX extends AbstractNegotiationParty {

	private Bid lastReceivedBid = null;
	private int nrChosenActions = 0; // number of times chosenAction was called.
	private StandardInfoList history;

	@Override
	public void init(NegotiationInfo info) {

		super.init(info);

		System.out.println("Discount Factor is " + info.getUtilitySpace().getDiscountFactor());
		System.out.println("Reservation Value is " + info.getUtilitySpace().getReservationValueUndiscounted());

		if (getData().getPersistentDataType() != PersistentDataType.STANDARD) {
			throw new IllegalStateException("need standard persistent data");
		}
		history = (StandardInfoList) getData().get();

		if (!history.isEmpty()) {
			// example of using the history. Compute for each party the maximum
			// utility of the bids in last session.
			Map<String, Double> maxutils = new HashMap<String, Double>();
			StandardInfo lastinfo = history.get(history.size() - 1);
			for (Tuple<String, Double> offered : lastinfo.getUtilities()) {
				String party = offered.get1();
				Double util = offered.get2();
				maxutils.put(party, maxutils.containsKey(party) ? Math.max(maxutils.get(party), util) : util);
			}
			System.out.println(maxutils); // notice tournament suppresses all
											// output.
		}
	}

	@Override
	public Action chooseAction(List<Class<? extends Action>> validActions) {
		nrChosenActions++;
		if (nrChosenActions > history.size() & lastReceivedBid != null) {
			return new Accept(getPartyId(), lastReceivedBid);
		} else {
			return new Offer(getPartyId(), generateRandomBid());
		}
	}

	@Override
	public void receiveMessage(AgentID sender, Action action) {
		super.receiveMessage(sender, action);
		if (action instanceof Offer) {
			lastReceivedBid = ((Offer) action).getBid();
		}
	}

	@Override
	public String getDescription() {
		return "accept Nth offer";
	}

}
