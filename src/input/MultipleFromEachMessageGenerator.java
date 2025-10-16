/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.Settings;
import core.SettingsError;

/**
 * Message creation -external events generator. Creates one message from
 * every source node (defined with {@link MessageEventGenerator#HOST_RANGE_S})
 * to one of the destination nodes (defined with
 * {@link MessageEventGenerator#TO_HOST_RANGE_S}).
 * The message size, first messages time and the intervals between creating
 * messages can be configured like with {@link MessageEventGenerator}. End
 * time is not respected, but messages are created until every from-node has
 * created a message.
 * 
 * @see MessageEventGenerator
 */
public class MultipleFromEachMessageGenerator extends MessageEventGenerator {
	private List<Integer> fromIds;
	private Integer currentIndex = 0;
	private List<MessageCreateEvent> events = new ArrayList<MessageCreateEvent>();

	public MultipleFromEachMessageGenerator(Settings s) {
		super(s);
		this.fromIds = new ArrayList<Integer>();
		for (int i = hostRange[0]; i < hostRange[1]; i++) {
			fromIds.add(i);
		}

		if (toHostRange == null) {
			// throw new SettingsError("Destination host (" + TO_HOST_RANGE_S +
			// ") must be defined");
			this.toHostRange = hostRange;
		}
		Collections.shuffle(fromIds, rng);
		this.generateEvents();
	}

	void generateEvents() {
		ArrayList<Double> lastTimes = new ArrayList<Double>();
		for (int i = 0; i < this.fromIds.size(); i++) {
			lastTimes.add(0.0);
		}

		Double end = this.msgTime[1];
		Double lastTime = 0.0;
		Boolean ended = false;

		while (!ended) {
			Boolean roundEnded = true;
			for (int i = 0; i < this.fromIds.size(); i++) {
				Double time = lastTimes.get(i) + drawNextEventTimeDiff();
				lastTimes.set(i, time);

				if (time < end) {
					int from = this.fromIds.get(i);
					int to = drawToAddress(toHostRange, -1);
					int responseSize = 0;
					MessageCreateEvent mce = new MessageCreateEvent(from, to, getID(),
							drawMessageSize(), responseSize, time);
					this.events.add(mce);
					roundEnded = false;
				}
			}

			ended = roundEnded;
		}
	}

	/**
	 * Returns the next message creation event
	 * 
	 * @see input.EventQueue#nextEvent()
	 */
	public ExternalEvent nextEvent() {
		if (this.events.size() == 0) { /* oops, no more from addresses */
			this.nextEventsTime = Double.MAX_VALUE;
			return new ExternalEvent(Double.MAX_VALUE);
		} else {
			MessageCreateEvent event = this.events.remove(0);
		// if (this.events.size() == 0) { /* oops, no more from addresses */
		// 	this.nextEventsTime = Double.MAX_VALUE;
		// } else {
		// 	this.nextEventsTime = this.events.get(0).time;
		// }
			this.nextEventsTime = event.time;
			return event;
		}
// 		int responseSize = 0; /* no responses requested */
// 		int from;
// 		int to;

// 		from = this.fromIds.get(this.currentIndex);

// 		to = drawToAddress(toHostRange, -1);

// 		if (to == from) { /* skip self */
// 		}

// 		Double time = this.nextEventsTimes.get(this.currentIndex) + drawNextEventTimeDiff();

// 		this.nextEventsTimes.set(this.currentIndex, time);

// 		if (this.currentIndex + 1 >= this.events.size()) {
// 			/* next event would be later than the end time */
// 			this.nextEventsTime = Double.MAX_VALUE;
// 			return new ExternalEvent(Double.MAX_VALUE);
// 		} else {
// MessageCreateEvent event = this.events
// 			this.currentIndex++;

// 		}
// 		this.nextEventsTime = this.nextEventsTimes.get(this.currentIndex);
// 		System.out.println("");
// 		System.out.println(this.nextEventsTime);
// 		System.out.println(this.currentIndex);

// 		if (this.msgTime != null && this.nextEventsTime > this.msgTime[1]) {
// 			/* next event would be later than the end time */
// 			this.nextEventsTime = Double.MAX_VALUE;
// 			return new ExternalEvent(Double.MAX_VALUE);
// 		}

// 		MessageCreateEvent mce = new MessageCreateEvent(from, to, getID(),
// 				drawMessageSize(), responseSize, time);

// 		return mce;
	}

}
