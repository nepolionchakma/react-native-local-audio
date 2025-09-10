import { NativeModules } from "react-native";

export type Song = {
  id: string;
  title: string;
  artist: string;
  album: string;
  path: string;
  cover: string;
};

export type GetAllAudioOptions = {
  sortBy?: "TITLE" | "ARTIST" | "ALBUM" | "DATE";
  orderBy?: "ASC" | "DESC";
  limit?: number;
  offset?: number;
  coverQuality?: number;
};

type AudioModuleType = {
  getAllAudio(options?: GetAllAudioOptions): Promise<Song[]>;
  getSongsByAlbum(): Promise<Song[]>;
  searchSongsByTitle(options: { title: string }): Promise<Song[]>;
};

const { AudioModule } = NativeModules;

export default AudioModule as AudioModuleType;
