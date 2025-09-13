import { NativeModules } from "react-native";

export type Song = {
  id: number; // Changed from string to number
  title: string;
  artist: string;
  album: string;
  url: string; // Changed from path to url
  artwork: string; // Changed from cover to artwork
};

export type GetAllAudioOptions = {
  sortBy?: "TITLE" | "ARTIST" | "ALBUM" | "DATE";
  orderBy?: "ASC" | "DESC";
  limit?: number;
  offset?: number;
  artworkQuality?: number; // Changed from coverQuality to artworkQuality
};

type AudioModuleType = {
  getAllAudio(options?: GetAllAudioOptions): Promise<Song[]>;
  getSongsByAlbum(): Promise<Song[]>;
  searchSongsByTitle(options: { title: string }): Promise<Song[]>;
};

const { AudioModule } = NativeModules;

export default AudioModule as AudioModuleType;
