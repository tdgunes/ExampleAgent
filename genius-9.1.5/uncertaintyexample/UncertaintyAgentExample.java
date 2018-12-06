package uncertaintyexample;

import java.util.List;

import genius.core.Bid;
import genius.core.actions.Accept;
import genius.core.actions.Action;
import genius.core.actions.Offer;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.uncertainty.AdditiveUtilitySpaceFactory;
import genius.core.uncertainty.ExperimentalUserModel;
import genius.core.utility.AbstractUtilitySpace;

public class UncertaintyAgentExample extends AbstractNegotiationParty {

	@Override
	public Action chooseAction(List<Class<? extends Action>> possibleActions) {
		log("This is the UncertaintyAgentExample.");
		log("The user model is: " + userModel);
		log("The default estimated utility space is: " + getUtilitySpace());

		Bid randomBid = getUtilitySpace().getDomain().getRandomBid(rand);
		log("The default estimate of the utility of a random bid + " + randomBid
				+ " is: " + getUtility(randomBid));

		if (userModel instanceof ExperimentalUserModel) {
			log("You have given the agent access to the real utility space for debugging purposes.");
			ExperimentalUserModel e = (ExperimentalUserModel) userModel;
			AbstractUtilitySpace realUSpace = e.getRealUtilitySpace();

			log("The real utility space is: " + realUSpace);
			log("The real utility of the random bid is: "
					+ realUSpace.getUtility(randomBid));
		}

		// Sample code that accepts offers that appear in the top 10% of offers
		// in the user model
		if (getLastReceivedAction() instanceof Offer) {
			Bid receivedBid = ((Offer) getLastReceivedAction()).getBid();
			List<Bid> bidOrder = userModel.getBidRanking().getBidOrder();

			// If the rank of the received bid is known
			if (bidOrder.contains(receivedBid)) {
				double percentile = (bidOrder.size()
						- bidOrder.indexOf(receivedBid))
						/ (double) bidOrder.size();
				if (percentile < 0.1)
					return new Accept(getPartyId(), receivedBid);
			}
		}

		// Otherwise, return a random offer
		return new Offer(getPartyId(), generateRandomBid());
	}

	private void log(String s) {
		System.out.println(s);
	}

	/**
	 * With this method, you can override the default estimate of the utility
	 * space given uncertain preferences specified by the user model. This
	 * example sets every value to zero.
	 */
	@Override
	public AbstractUtilitySpace estimateUtilitySpace() {
		return new AdditiveUtilitySpaceFactory(getDomain()).getUtilitySpace();
	}

	@Override
	public String getDescription() {
		return "Example agent that can deal with uncertain preferences";
	}

}
