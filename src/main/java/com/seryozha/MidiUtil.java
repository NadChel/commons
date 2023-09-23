package org.example.shared.utilities;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

public class MidiUtil {
    public static MidiEventBuilder getMidiEventBuilder() {
        return new MidiEventBuilder();
    }
    public static class MidiEventBuilder {
        private int command;
        private int channel;
        private int firstByte;
        private int secondByte;
        private int tick;

        public MidiEventBuilder withCommand(int command) {
            this.command = command;
            return this;
        }

        public MidiEventBuilder withChannel(int channel) {
            this.channel = channel;
            return this;
        }

        public MidiEventBuilder withFirstByte(int firstByte) {
            this.firstByte = firstByte;
            return this;
        }

        public MidiEventBuilder withSecondByte(int secondByte) {
            this.secondByte = secondByte;
            return this;
        }

        public MidiEventBuilder withTick(int tick) {
            this.tick = tick;
            return this;
        }

        public MidiEvent build() {
            return Util.wrapInTryCatchAndGet(() -> {
                ShortMessage message = new ShortMessage(command, channel, firstByte, secondByte);
                return new MidiEvent(message, tick);
            });
        }
    }
}
